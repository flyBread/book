package com.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookies操作
 * 
 * @author haoxw
 * @since 2013/4/3
 */
public class CookiesUtil {
  private final static Logger logger = LoggerFactory.getLogger(CookiesUtil.class);
  /**
   * 用户登录标记
   */
  public final static String loginFlag = "gongzuoquan.info";
  //来源渠道标记
  public final static String channelFlag = "Channel";

  private static class InstanceHolder {
    private static CookiesUtil instance = new CookiesUtil();
  }

  private CookiesUtil() {

  }

  public static CookiesUtil getInstance() {
    return InstanceHolder.instance;
  }

  /**
   * 获取当前用户cookies值
   */
  public String getCookieValue(HttpServletRequest request) {
    String result = null;
    String uri = request.getRequestURI();
    if (StringUtils.contains(uri, "/rest/")) {
      String auth = request.getHeader("Authorization");
      if (StringUtils.isNotBlank(auth)) result = ThreeDes.decrypt(auth);
    } else {
      Cookie[] cookies = request.getCookies();
      if (cookies != null && cookies.length > 0) for (Cookie cookie : cookies) {
        if (cookie.getName().equals(loginFlag)) {
          try {
            result = ThreeDes.decrypt(cookie.getValue());
          } catch (Exception e) {
            logger.error("getCookieValue", e);
          }
          break;
        }
      }
    }
    // userId|deviceType|deviceId|token|lastLoginTime
    if (StringUtils.isNotBlank(result)) {
      String[] cookieValue = result.split("[|]", 6);
      if (cookieValue.length < 5) result = null;
    }
    return result;
  }

  public boolean isCookieValid(String encryptCookie) {
    String result = null;
    if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(encryptCookie))) {
      try {
        result = ThreeDes.decrypt(encryptCookie);
      } catch (Exception e) {
        logger.error("isCookieValid", e);
      }
    }
    if (StringUtils.isNotEmpty(result)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      int clenth = result.split("[|]").length;
      return clenth == 5 || clenth == 6;
    }
    return false;
  }

  /**
   * 获取当前用户cookies值，并已拆分为数组
   */
  public String[] getCookieArrayValue(HttpServletRequest request) {
    String result = null;
    String uri = request.getRequestURI();
    if (StringUtils.contains(uri, "/web/")) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null && cookies.length > 0) for (Cookie cookie : cookies) {
        if (cookie.getName().equals(loginFlag)) {
          try {
            result = ThreeDes.decrypt(cookie.getValue());
          } catch (Exception e) {
            logger.error("getCookieValue", e);
          }
          break;
        }
      }
    } else {
      String auth = request.getHeader("Authorization");
      if (StringUtils.isNotBlank(auth)) result = ThreeDes.decrypt(auth);
    }
    // userId|deviceType|deviceId|token|lastLoginTime
    if (StringUtils.isNotBlank(result)) {
      String[] cookieValue = result.split("[|]", 6);
      if (cookieValue.length >= 5) return cookieValue;
    }
    return null;
  }

  /**
   * 添加登录用户cookies
   */
  public Cookie addCookie(int expireSeconds, String value, String cookieName,
      HttpServletResponse response) {
    Cookie cookie = null;
    try {
      if (StringUtils.isEmpty(cookieName)) {
        cookieName = loginFlag;
      }
      cookie = new Cookie(cookieName, value);
      cookie.setPath("/");
      cookie.setMaxAge(expireSeconds);// cookie保存7天
      response.addCookie(cookie);
      logger.info("add cookies value=" + value);
    } catch (Exception e) {
      logger.error("", e);
    }
    return cookie;
  }

  /**
   * 添加登录用户email作为cookies
   */
  public Cookie addCookie(int expireSeconds, String code, HttpServletResponse response) {
    return this.addCookie(expireSeconds, ThreeDes.encrypt(code), null, response);
  }

  /**
   * 退出 移除cookies
   */
  public boolean removeCookie(HttpServletRequest request, HttpServletResponse response) {
    boolean b = false;
    Cookie[] cookies = request.getCookies();
    // cookies不为空，则清除
    if (cookies != null) {
      try {
        for (int i = 0; i < cookies.length; i++) {
          if (StringUtils.equals(loginFlag, cookies[i].getName())) {
            Cookie cookie = new Cookie(cookies[i].getName(), null);
            cookie.setMaxAge(0);
            cookie.setPath("/");// 根据你创建cookie的路径进行填写
            response.addCookie(cookie);
            break;
          }
        }
      } catch (Exception e) {
        logger.error("removeCookie", e);
      }
      b = true;
    }
    return b;
  }

  public long getUserId(HttpServletRequest request) {
    long userId = 0L;
    String cookieValue = getCookieValue(request);
    if (StringUtils.isNotEmpty(cookieValue)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      String[] decryptValue = cookieValue.split("[|]", 6);
      if (decryptValue.length >= 5) {
        return Long.parseLong(decryptValue[0]);
      }
    }
    return userId;
  }

  public String getToken(HttpServletRequest request) {
    String cookieValue = getCookieValue(request);
    if (StringUtils.isNotEmpty(cookieValue)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      String[] decryptValue = cookieValue.split("[|]", 6);
      if (decryptValue.length >= 5) {
        return decryptValue[3];
      }
    }
    return null;
  }

  public long getLastLoginTime(HttpServletRequest request) {
    String cookieValue = getCookieValue(request);
    if (StringUtils.isNotEmpty(cookieValue)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      String[] decryptValue = cookieValue.split("[|]", 6);
      if (decryptValue.length >= 5) {
        return Long.valueOf(decryptValue[4]);
      }
    }
    return 0;
  }

  public String getUserDeviceType(HttpServletRequest request) {
    String cookieValue = getCookieValue(request);
    if (StringUtils.isNotEmpty(cookieValue)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      String[] decryptValue = cookieValue.split("[|]", 6);
      if (decryptValue.length >= 5) {
        return decryptValue[1];
      }
    }
    return null;
  }

  public String getUserDeviceId(HttpServletRequest request) {
    String cookieValue = getCookieValue(request);
    if (StringUtils.isNotEmpty(cookieValue)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      String[] decryptValue = cookieValue.split("[|]", 6);
      if (decryptValue.length >= 5) {
        return decryptValue[2];
      }
    }
    return null;
  }

  public String getCurVersion(HttpServletRequest request) {
    String cookieValue = getCookieValue(request);
    if (StringUtils.isNotEmpty(cookieValue)) {
      // userId|deviceType|deviceId|token|lastLoginTime
      String[] decryptValue = cookieValue.split("[|]", 6);
      if (decryptValue.length == 6) {
        return decryptValue[5];
      }
    }
    return null;
  }

  /**
   * 获取当前用户渠道值，比如小米商店、迪信通等，主要是安卓端用，便于统计当前渠道包活跃用户以及访问跟踪
   */
  public String getChannelFromRequest(HttpServletRequest request) {
    String result = null;
    String uri = request.getRequestURI();
    if (StringUtils.contains(uri, "/rest/")) {
      result = request.getHeader(channelFlag);
    } else {
      Cookie cookie = CookieUtils.getCookie(request, channelFlag);
      if (cookie != null) {
        result = cookie.getValue();
      }
    }
    return result;
  }
  /**
   * 根据版本号判断是否是https请求
   * @return true:是https请求；false：http请求
   */
  public boolean isHttps(HttpServletRequest request){
    boolean isHttps = false;
    String appVersion = getCurVersion(request);
    if (StringUtils.isNotBlank(appVersion)) {
      try {
        appVersion = appVersion.substring(0,5);
        //3.0.6以后都是https请求
        if (appVersion.compareTo("3.0.6") >= 0) {
          isHttps = true;
        }
      } catch (Exception e) {
        logger.error("isHttps :  ", e);
      }
    }
    return isHttps;
  }
}

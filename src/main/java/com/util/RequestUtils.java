package com.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet操作类
 * 
 * @author xuewuhao
 */
public class RequestUtils {

  private static final Logger log = LoggerFactory.getLogger(RequestUtils.class);

  /**
   * 获取requet中，类型为Short的key
   * 
   * @param request
   *          需要解析的HttpServletRequest
   * @param key
   *          需要取出的属性名
   * @param defaultValue
   *          设置默认值
   * @return Short类型的属性值
   */
  public static short getShort(HttpServletRequest request, String key, short defaultValue) {
    String str = request.getParameter(key);
    return ParamUtils.getShort(str, defaultValue);
  }

  /**
   * 获取requet中，类型为Int的key
   * 
   * @param request
   *          需要解析的HttpServletRequest
   * @param key
   *          需要取出的属性名
   * @param defaultValue
   *          设置默认值
   * @return Int类型的属性值
   */
  public static int getInt(HttpServletRequest request, String key, int defaultValue) {
    String str = request.getParameter(key);
    return ParamUtils.getInt(str, defaultValue);
  }

  /**
   * 获取requet中，类型为Long的key
   * 
   * @param request
   *          需要解析的HttpServletRequest
   * @param key
   *          需要取出的属性名
   * @param defaultValue
   *          设置默认值
   * @return Long类型的属性值
   */
  public static long getLong(HttpServletRequest request, String key, long defaultValue) {
    String str = request.getParameter(key);
    return ParamUtils.getLong(str, defaultValue);
  }

  /**
   * 获取requet中，类型为String的key
   * 
   * @param request
   *          需要解析的HttpServletRequest
   * @param key
   *          需要取出的属性名
   * @param defaultValue
   *          设置默认值
   * @return String类型的属性值
   */
  public static String getString(HttpServletRequest request, String key, String defaultValue) {
    String str = request.getParameter(key);
    return ParamUtils.getString(str, defaultValue);
  }

  /**
   * 获取requet中，类型为String的key，如果传入的值不在合法值集合中，则返回默认值
   * 
   * @param request
   *          需要解析的HttpServletRequest
   * @param key
   *          需要取出的属性名
   * @param validValues
   *          合法值
   * @param defaultValue
   *          设置默认值
   * @return String类型的属性值
   */
  public static String getString(HttpServletRequest request, String key, String[] validValues,
      String defaultValue) {
    boolean caseSensitive = false;
    return getString(request, key, validValues, defaultValue, caseSensitive);
  }

  /**
   * 获取requet中，类型为String的key，如果传入的值不在合法值集合中，则返回默认值
   * 
   * @param request
   *          需要解析的HttpServletRequest
   * @param key
   *          需要取出的属性名
   * @param validValues
   *          可以使用的值
   * @param defaultValue
   *          合法值
   * @param caseSensitive
   *          大小写是否敏感
   * @return String类型的属性值
   */
  public static String getString(HttpServletRequest request, String key, String[] validValues,
      String defaultValue, boolean caseSensitive) {
    String str = request.getParameter(key);
    return ParamUtils.getString(str, validValues, defaultValue, caseSensitive);
  }

  /**
   * 设置缓存过期时间
   * 
   * @param response
   *          响应头
   * @param unit
   *          时间单元
   * @param time
   *          过期时间
   */
  public static void setCacheHeader(HttpServletResponse response, TimeUnit unit, long time) {
    response.setHeader("Pragma", "Public");
    // HTTP 1.1
    response.setHeader("Cache-Control", "max-age=" + unit.toSeconds(time));
    response.setDateHeader("Expires", System.currentTimeMillis() + unit.toMillis(time));
  }

  /**
   * 设置过期时间，增加CDN和浏览器缓存策略
   * 
   * @param response
   *          响应头
   * @param time
   *          过期时间，单位秒
   */
  public static void setCacheHeader(HttpServletResponse response, long time) {
    setCacheHeader(response, TimeUnit.SECONDS, time);
  }

  private static final String callBackFormat = "%s(%s);";

  private static final String jsonpFormat = "var %s=%s;";

  /**
   * 
   * @param request
   * @return
   */
  public static String dump(HttpServletRequest request) {
    StringBuffer sb = new StringBuffer();

    Enumeration names = request.getAttributeNames();
    sb.append("\nrequest attributes: \n");
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      sb.append("name = [" + name + "] value = [" + request.getAttribute(name) + "]\n");
    }

    names = request.getParameterNames();
    sb.append("\nrequest parameter: \n");
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      sb.append("name = [" + name + "] value = [" + request.getParameter(name) + "]\n");
    }
    return sb.toString();
  }

  /**
   * 
   * @param request
   * @return
   */
  public static boolean isAjaxRequest(HttpServletRequest request) {
    String xReq = request.getHeader("X-Requested-With");
    return (xReq != null);
  }

  /**
   * @param request
   * @return
   */
  public static String getRefer(HttpServletRequest request) {
    return request.getHeader("REFERER");
  }

  /**
   * 
   * @param request
   * @return
   */
  public static String getRequestCompleteURL(HttpServletRequest request) {
    return request.getRequestURL().append("?").append(request.getQueryString()).toString();
  }

  /**
   * 
   * @param response
   * @param name
   * @param value
   * @param expiry
   * @param domain
   * @param path
   *          .
   */
  public static void setCookie(HttpServletResponse response, String name, String value, int expiry,
      String domain, String path) {
    if (log.isDebugEnabled()) {
      log.debug("Setting cookie '" + name + " [ " + value + " ] ' on path '" + path + "'");
    }

    Cookie cookie = new Cookie(name, value);
    cookie.setSecure(false);
    if (domain != null) cookie.setDomain(domain);

    cookie.setPath(path != null ? path : "/");

    cookie.setMaxAge(expiry); // 30 days

    response.addCookie(cookie);
  }

  /**
   * 
   * @param response
   * @param name
   * @param domain
   * @param path
   *          .
   */
  public static void deleteCookie(HttpServletResponse response, String name, String domain,
      String path) {
    Cookie cookie = new Cookie(name, "");
    deleteCookie(response, cookie, domain, path);
  }

  /**
   * 批量删除某个域名下的cookie
   * 
   * @param response
   * @param names
   * @param domain
   * @param path
   */
  public static void deleteCookies(HttpServletResponse response, String[] names, String domain,
      String path) {
    if (null == names) {
      return;
    }
    for (int i = 0; i < names.length; i++) {
      Cookie cookie = new Cookie(names[i], "");
      deleteCookie(response, cookie, domain, path);
    }

  }

  /**
   * 
   * @param response
   * @param cookie
   * @param domain
   * @param path
   *          .
   */
  public static void deleteCookie(HttpServletResponse response, Cookie cookie, String domain,
      String path) {
    if (cookie != null) {
      if (log.isDebugEnabled()) {
        log.debug("Deleting cookie '" + cookie.getName() + "' on domain '" + cookie.getDomain()
            + "' path '" + path + "'");
      }
      // Delete the cookie by setting its maximum age to zero
      cookie.setMaxAge(0);
      if (domain != null) cookie.setDomain(domain);
      cookie.setPath(path != null ? path : "/");
      response.addCookie(cookie);
    }
  }

  /**
   * 
   * @param request
   * @param name
   * @return .
   */
  public static Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    Cookie returnCookie = null;

    if (cookies == null) {
      return returnCookie;
    }

    for (int i = 0; i < cookies.length; i++) {
      Cookie thisCookie = cookies[i];

      if (thisCookie.getName().equals(name)) {
        // cookies with no value do me no good!
        if (!thisCookie.getValue().equals("")) {
          returnCookie = thisCookie;

          break;
        }
      }
    }

    return returnCookie;
  }

  /**
   * 获取Client IP : 此方法能够穿透squid 和 proxy
   * 
   * @param request
   * @return .
   */
  public static String getClientIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("dian-remote");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("x-forwarded-for");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip.indexOf(",") > 0) ip = ip.substring(0, ip.indexOf(","));
    return ip;
  }

  /**
   * 获取client端的实际ip地址 之所以封装，是因为需要处理使用squid的情况
   * 
   * @param request
   * @return
   */
  public static String getRemoteAddr(HttpServletRequest request) {
    String remoteAddr = request.getHeader("X-Forwarded-For");
    if (remoteAddr != null) {
      return remoteAddr.split(",")[0];
    }
    return request.getRemoteAddr();
  }

  /**
   * 判断是否为工作圈的请求
   * */
  @Deprecated
  public static Boolean isGongZuoQuanReq(HttpServletRequest request) {
    if (request != null) {
      String refer = getRefer(request);
      try {
        if (!StringUtils.isEmpty(refer)) {
          URL url = new URL(refer);
          String host = url.getHost();
          if (host.startsWith("www.gongzuoquan.com") || host.startsWith("gongzuoquan.com")
              || host.startsWith("gzq.chanjet.com")) {
            return true;
          }
        }

      } catch (MalformedURLException e) {
        // 不能够生成URL，说明请求不是遵循http协议的
        log.error("error request url: " + refer);
      }
    }
    return false;
  }
  
  /**
   * 从request中获取参数信息
   * @param request
   * @return
   */
  public static String getParams(final HttpServletRequest request) {
    Enumeration e = request.getParameterNames();
    StringBuilder builder = new StringBuilder();
    builder.append("Parameters: ");
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();

      String value = request.getParameter(name);
      builder.append(name).append("=").append(value).append(" ");
    }
    return builder.toString();
  }
  
  /**
   * 从request中获取请求头信息
   * @param request
   * @return
   */
  public static String getHeaders(final HttpServletRequest request) {
    @SuppressWarnings("rawtypes")
    Enumeration e = request.getHeaderNames();

    StringBuilder builder = new StringBuilder();
    builder.append("Headers: ");
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      String value = request.getHeader(name);
      builder.append(name).append("=").append(value).append(" ");
    }
    return builder.toString();
  }

}

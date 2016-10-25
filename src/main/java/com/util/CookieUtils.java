package com.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie
 * @author haoxw
 *
 */
public class CookieUtils {
  /**
   * Logger for this class
   */
  private static final Log log = LogFactory.getLog(CookieUtils.class);

  public static final String DEFAULT_DOMAIN = ("gzq.chanjet.com");

  /**
   * Convenience method to set a cookie
   * 
   * @param response
   * @param name
   * @param value
   * @param path
   */
  public static void setCookie(HttpServletResponse response, String name, String value) {
    CookieUtils.setCookie(response, name, value, null, null, -1);
  }

  public static void setCookie(HttpServletResponse response, String name, String value,
      String domain, String path, int maxAge) {

    if (name == null || value == null) {
      log.warn("name or value can't be null, name=" + name + " value=" + value);
      return;
    }

    Cookie cookie = new Cookie(name, value);
    cookie.setSecure(false);

    //    if (domain == null) {
    //      domain = DEFAULT_DOMAIN;
    //    }
    //    cookie.setDomain(domain);

    path = (path == null ? "/" : path);
    cookie.setPath(path);

    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  /**
   * Convenience method to get a cookie by name
   * 
   * @param request
   *            the current request
   * @param name
   *            the name of the cookie to find
   * 
   * @return the cookie (if found), null if not found
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
   * @param request
   * @param name
   * @return
   */
  public static String getCookieValue(HttpServletRequest request, String name) {
    Cookie cookie = getCookie(request, name);
    if (cookie == null) return null;
    return cookie.getValue();
  }

  /**
   * Convenience method for deleting a cookie by name
   * 
   * @param response
   *            the current web response
   * @param cookie
   *            the cookie to delete
   * @param path 
   */
  public static void deleteCookie(HttpServletResponse response, Cookie cookie, String domain,
      String path) {
    if (cookie != null) {
      // Delete the cookie by setting its maximum age to zero
      cookie.setMaxAge(0);

      if (log.isDebugEnabled()) {
        log.debug("deleteCookie(HttpServletResponse, Cookie) -  : cookie=" + cookie); //$NON-NLS-1$
      }

      if (domain == null) {
        domain = DEFAULT_DOMAIN;
      }
      cookie.setDomain(domain);
      cookie.setValue("");

      path = (path == null ? "/" : path);
      cookie.setPath(path);

      response.addCookie(cookie);
    }
  }

}

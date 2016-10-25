package com.util;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class WebResponseResultLogAop implements MethodInterceptor {
  public static final Logger logger = LoggerFactory.getLogger(WebResponseResultLogAop.class);

  public Object invoke(MethodInvocation invocation) throws Throwable {
    Object[] arguments = invocation.getArguments();
    String requestURI = "";
    String responseResult = "";
    String requestParams = "";
    String headers = "";
    long costTime;
    JSONObject restAopData = new JSONObject();
    boolean isHttps = true;
    long userId = 0;
    //获取uri,请求参数，请求头，用户id
    if (arguments != null && arguments.length > 0)
      for (Object argument : arguments) {
        if (argument instanceof HttpServletRequest) {
          HttpServletRequest request = (HttpServletRequest) argument;

          // construct data from requet
          restAopData = getDataFromRequest(restAopData, request);

          CookiesUtil cookiesUtil = CookiesUtil.getInstance();
          requestURI = request.getRequestURI();
          requestParams = RequestUtils.getParams(request);
          headers = RequestUtils.getHeaders(request);
          String[] headerValue = headers.split(":");
          restAopData.put("Headers", headerValue.length > 1?headerValue[1].trim():"");
          userId = cookiesUtil.getUserId(request);
          break;
        }
      }

    /*
      count execute time
     */
    long s = System.currentTimeMillis();
    restAopData.put("requestTime", s);

    Object result = invocation.proceed();

    long e = System.currentTimeMillis();
    restAopData.put("responseTime", e);
    costTime = e - s;
    restAopData.put("costTime", costTime);

    try {
      if (result instanceof HttpEntity) {
        ResponseEntity<String> entity = (ResponseEntity<String>) result;
        //如果是http请求，替换返回结果中的地址
        responseResult = entity.getBody();
      }
    } catch (Exception e2) {
      logger.error("error", e2);
    }

    // insert responseBody
    restAopData.put("responseBody", responseResult);


    logger.info("requestURI= {},userId={}, {}, responseResult= {}, {}, use time = {}ms",
        requestURI, userId, requestParams, responseResult, headers, e - s);
    return result;
  }

  /**
   * 从request中获取数据
   * @throws JSONException
   */
  private JSONObject getDataFromRequest(JSONObject jsonObject, HttpServletRequest request)
      throws JSONException {
    CookiesUtil cookiesUtil = CookiesUtil.getInstance();
    jsonObject.put("gzqCookie", cookiesUtil.getCookieValue(request));
    jsonObject.put("channel", cookiesUtil.getChannelFromRequest(request));
    jsonObject.put("requestUrl", request.getRequestURI());
    jsonObject.put("requestParam", request.getParameterMap());
    return jsonObject;
  }

}

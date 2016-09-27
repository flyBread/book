package com.util;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月21日——下午10:50:20
 * 
 */
public class ValiteUtil {
  /**
   * 判断参数的类型
   * 
   * @param args
   * @return
   */
  public static boolean validNull(Object... args) {
    boolean flag = true;
    for (Object s : args) {
      if (s == null) {
        return flag;
      }

      //字符串的变量，不能是"" 的空字符串
      if (s instanceof String) {
        if (((String) s).length() <= 0) {
          return flag;
        }
      }

      //Long 类型的值不能是0
      if (s instanceof Long) {
        if (((Long) s).longValue() == 0) {
          return flag;
        }
      }
    }
    return !flag;
  }

  public static boolean validNotNull(Object... objects) {
    return !validNull(objects);
  }
}

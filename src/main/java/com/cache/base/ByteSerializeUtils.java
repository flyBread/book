package com.cache.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月18日——下午3:44:20
 * 
 */
public class ByteSerializeUtils {

  public static Logger logger = LoggerFactory.getLogger(ByteSerializeUtils.class);

  public byte[] serialize(Object value) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    if (value == null) {
      throw new NullPointerException("Can't serialize null");
    }
    byte[] result = null;

    try {
      ObjectOutputStream os = new ObjectOutputStream(bos);
      os.writeObject(value);
      os.close();
      bos.close();
      result = bos.toByteArray();
    }
    catch (IOException e) {
      throw new IllegalArgumentException("Non-serializable object", e);
    }
    finally {
      close(bos);
      bos = null;// make gc do its work
    }
    return result;
  }

  public Object deserialize(byte[] in) {
    Object result = null;
    ByteArrayInputStream bis = null;
    ObjectInputStream is = null;
    try {
      if (in != null) {
        bis = new ByteArrayInputStream(in);
        is = new ObjectInputStream(bis);
        result = is.readObject();
        is.close();
        bis.close();
      }
    }
    catch (IOException e) {
      logger.info(String.format("Caught IOException decoding %d bytes of data", in == null ? 0
          : in.length) + e);
    }
    catch (ClassNotFoundException e) {
      logger.info(String.format("Caught CNFE decoding %d bytes of data", in == null ? 0 : in.length)
          + e);
    }
    finally {
      close(is);
      close(bis);
      is = null;
      bis = null;
    }
    return result;
  }

  public static void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      }
      catch (Exception e) {
        logger.info("Unable to close " + closeable, e);
      }
    }
  }
}

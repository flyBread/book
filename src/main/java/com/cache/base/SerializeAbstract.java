package com.cache.base;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月18日——下午3:49:15
 * 
 */
public abstract class SerializeAbstract {

  public abstract byte[] serialize(Object value);

  public abstract Object deserialize(byte[] in);

}

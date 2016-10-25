package com.cache.base;

import com.util.ValiteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

public class CacheBase<T> {

	private RedisPool cache = RedisPool.getInstance("/reis/cache");
	private static Logger logger = LoggerFactory.getLogger(CacheBase.class);
	private ByteSerializeUtils util = new ByteSerializeUtils();

	public byte[] getKey(long key) {
		String rKey = Long.toString(key);
		return rKey.getBytes(Charset.forName("UTF-8"));
	}

	public byte[][] getKey(List<String> keyList) {
		byte keyBtArr[][] = new byte[keyList.size()][];
		for (int j = 0; j < keyList.size(); j++) {
			keyBtArr[j] = keyList.get(j).getBytes(Charset.forName("UTF-8"));
		}
		return keyBtArr;
	}

	private byte[] getKey(String key) {
		String rKey = key;
		return rKey.getBytes(Charset.forName("UTF-8"));
	}

	/**
	 * key -value 样式的设置
	 */
	public void set(final Object key, final Object obj) {
		ShardedJedis jedis = cache.getJedis();
		byte[] keybyte = getKey(key.toString());
		byte[] valuebyte = util.serialize(obj);
		jedis.set(keybyte, valuebyte);
		returnJedis(jedis);
	}

	// 设置缓存
	public Object get(final Object key, final Object expected) {
		ShardedJedis jedis = cache.getJedis();
		byte[] resByte = jedis.get(getKey(key.toString()));
		returnJedis(jedis);
		return util.deserialize(resByte);
	}

	// 设置缓存
	public Object get(final Object key) {
		ShardedJedis jedis = cache.getJedis();
		byte[] resByte = jedis.get(getKey(key.toString()));
		returnJedis(jedis);
		return util.deserialize(resByte);
	}

	public void del(final Object key) {
		ShardedJedis jedis = cache.getJedis();
		jedis.del(getKey(key.toString()));
		returnJedis(jedis);
	}

	public void del(final Set<?> keys) {
		ShardedJedis jedis = cache.getJedis();
		ShardedJedisPipeline pline = jedis.pipelined();
		for (Object key : keys) {
			pline.del(getKey(key.toString()));
		}
		returnJedis(jedis);
	}

	public void hset(final String hName, final String key, final Object value) {
		ShardedJedis jedis = cache.getJedis();
		if (null != value) {
			jedis.hset(getKey(hName), getKey(key), util.serialize(value));
		}
		returnJedis(jedis);
	}

	public boolean setAdd(final String sName, final Object value) {
		ShardedJedis jedis = cache.getJedis();
		if (null != value) {
			Long res = jedis.sadd(getKey(sName), util.serialize(value));
			return res == 1;
		}
		returnJedis(jedis);
		return false;
	}

	public Set<Object> setGetAll(final String sName) {
		ShardedJedis jedis = getCache().getJedis();
		try {
			if (null != sName) {
				Set<byte[]> sets = jedis.smembers(getKey(sName));
				Set<Object> resultList = new HashSet<Object>();
				for (byte[] tmp : sets) {
					resultList.add(util.deserialize(tmp));
				}
				return resultList;
			}
		} catch (Exception e) {
			logger.error("setGetAll -> obj=" + sName, e);
			getCache().returnBrokenJedis(jedis);
		} finally {
			getCache().returnJedis(jedis);
		}
		return null;
	}

	@SuppressWarnings({})
	public Object hget(final String hkey, final String key) {
		ShardedJedis jedis = getCache().getJedis();
		byte[] rsp = jedis.hget(getKey(hkey), getKey(key));
		returnJedis(jedis);
		return util.deserialize(rsp);
	}

	public List<Object> hget(final String hkey, final List<String> key) {
		ShardedJedis jedis = cache.getJedis();
		List<byte[]> o = jedis.hmget(getKey(hkey), getKey(key));
		List<Object> resultList = new ArrayList<Object>();
		for (int i = 0; i < o.size(); i++) {
			resultList.add(util.deserialize(o.get(i)));
		}
		returnJedis(jedis);
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<T> hgetAll(final Object hkey) {
		ShardedJedis jedis = cache.getJedis();
		Map<byte[], byte[]> o = jedis.hgetAll(getKey(hkey.toString()));

		List<T> resultList = new ArrayList<T>();
		for (byte[] tmp : o.keySet()) {
			resultList.add((T) util.deserialize(o.get(tmp)));
		}
		returnJedis(jedis);
		return resultList;
	}

	public RedisPool getCache() {
		return cache;
	}

	public void setCache(RedisPool cache) {
		this.cache = cache;
	}

	public long zsetWithScore(Object zsetName, Double score, Object member) {
		ShardedJedis jedis = cache.getJedis();
		if (ValiteUtil.validNotNull(zsetName, score, member)) {
			Long res = jedis.zadd(getKey(zsetName.toString()), score.doubleValue(), util.serialize(member));
			returnJedis(jedis);
			return res;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public List<T> zsetGetALl(Object zsetName, long start, long end) {
		ShardedJedis jedis = cache.getJedis();
		if (ValiteUtil.validNotNull(zsetName)) {
			List<T> lists = new ArrayList<T>();
			Set<byte[]> res = jedis.zrange(getKey(zsetName.toString()), start, end);
			for (byte[] bs : res) {
				lists.add((T) util.deserialize(bs));
			}
			returnJedis(jedis);
			return lists;
		}
		return null;
	}

	public void returnJedis(ShardedJedis jedis) {
		cache.returnJedis(jedis);
	}

	public void expire(String voted, long l) {
		ShardedJedis jedis = cache.getJedis();
		jedis.expire(getKey(voted), (int) l);
		returnJedis(jedis);
	}

	/**
	 * 拆分对象的成员变量，保存成hashset的数据结构
	 **/
	public void hmset(String string, Object art) {
		Field[] fields = art.getClass().getDeclaredFields();
		ShardedJedis jedis = cache.getJedis();
		for (Field field : fields) {
			if (!field.getName().contains("serialVersionUID")) {
				Object obj = getFieldValue(field, art);
				String fieldname = field.getName();
				jedis.hset(getKey(string), getKey(fieldname), util.serialize(obj));
			}
		}
		returnJedis(jedis);
	}

	/**
	 * 根据类的成员信息，通过反射调用对应的get方法，获取对应的成员变量的值
	 **/
	private Object getFieldValue(Field field, Object art) {
		String fieldName = field.getName();
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1, fieldName.length());
		Method method = null;
		Object resultObj = null;
		try {
			// 得到get的方法信息
			method = art.getClass()
					.getMethod(methodName/* , new Class[] { field.getType() } */);
			resultObj = method.invoke(art /* , new Object[] { resultObj } */);
		} catch (Exception e) {
			logger.error("", e);
		}
		return resultObj;
	}

	@SuppressWarnings("unchecked")
	public List<T> zrevrange(String zsetName, long start, long end) {
		ShardedJedis jedis = cache.getJedis();
		Set<byte[]> value = jedis.zrevrange(getKey(zsetName), start, end);
		List<T> lists = new ArrayList<T>();
		for (byte[] bs : value) {
			lists.add((T) util.deserialize(bs));
		}
		returnJedis(jedis);
		return lists;
	}

}

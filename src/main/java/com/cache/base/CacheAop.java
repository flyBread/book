package com.cache.base;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.PostConstruct;

@Service("cacheAop")
public class CacheAop implements MethodInterceptor {
	/** 日志记录器 */
	public Logger logger = LoggerFactory.getLogger(CacheAop.class);
	private String cachePath;

	private RedisPool redisPool;

	/** 保存local对象 */
	ThreadLocal<ShardedJedis> socketThreadSafe = new ThreadLocal<ShardedJedis>();

	@PostConstruct
	public void init() {
		redisPool = RedisPool.getInstance(cachePath);
	}

	// Intercepts calls on an interface on its way to the target. These are
	// nested "on top" of the target.
	//
	// The user should implement the invoke(MethodInvocation) method to modify
	// the original behavior. E.g. the following class implements a tracing
	// interceptor (traces all the calls on the intercepted method(s)):
	//
	// class TracingInterceptor implements MethodInterceptor {
	// Object invoke(MethodInvocation i) throws Throwable {
	// System.out.println("method "+i.getMethod()+" is called on "+
	// i.getThis()+" with args "+i.getArguments());
	// Object ret=i.proceed();
	// System.out.println("method "+i.getMethod()+" returns "+ret);
	// return ret;
	// }
	// }

	public Object invoke(MethodInvocation arg0) throws Throwable {
		ShardedJedis jedis = null;
		boolean b = false;
		try {
			jedis = redisPool.getJedis();
			socketThreadSafe.set(jedis);
			Object ret = arg0.proceed();
			return ret;
		} catch (Exception e) {
			b = true;
			logger.error("error RedisReleaseCenter.invoke()", e);
			redisPool.returnBrokenJedis(jedis);
			throw new Exception(e);
		} finally {
			if (!b)
				redisPool.returnJedis(jedis);
			socketThreadSafe.remove();
		}
	}

	/**
	 * 取ShardedJedis
	 * 
	 * @return
	 */
	public ShardedJedis getJedis() {
		return socketThreadSafe.get();
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}
}

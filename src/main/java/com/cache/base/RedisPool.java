package com.cache.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RedisPool {

	public static final Logger logger = LoggerFactory.getLogger(RedisPool.class);
	// private static NutcrackerCenter instance = null;
	private static ConcurrentMap<String, RedisPool> map = new ConcurrentHashMap<String, RedisPool>();

	// redis 服务器地址
	private final static String redisHostP = "127.0.0.1:6379";

	private RedisPool(String zKeeperPath) {
		// IZkDataListener listener = new IZkDataListener() {
		// public void handleDataDeleted(String dataPath) throws Exception {
		// logger.info("!!! Redis node data has been deleted !!!" + dataPath);
		// }
		//
		// public void handleDataChange(String dataPath, byte[] data) throws
		// Exception {
		// logger.info("!!! Redis node data has been changed !!!" + dataPath);
		// String redisServerInf = DefaultStringUtils.toStr(data);
		// initialPool(dataPath);
		// logger.info("!!! Redis node[" + redisServerInf + "] connection pool
		// has been rebuild !!!"
		// + dataPath);
		// }
		// };
		// // 节点添加监控
		// zkHelp.subscribeDataChanges(zKeeperPath, listener);
		// 初始化 公用redis集群
		initialPool(zKeeperPath);
	}

	/**
	 * 实例化服务池
	 * 
	 * @param zkPath
	 *            内容是该节点下配置的nutcracker服务列表
	 * @return
	 */
	public synchronized static RedisPool getInstance(String zkPath) {
		if (zkPath == null) {
			return null;
		}
		RedisPool ret = map.get(zkPath);
		if (ret == null) {
			ret = new RedisPool(zkPath);
			map.put(zkPath, ret);
		}
		return ret;
	}

	private int MAX_ACTIVE = 30;
	private int MAX_IDLE = 50;
	private int MAX_WAIT = 1000;
	private int TIME_OUT = 3000;
	private boolean TEST_ON_BORROW = false;
	private boolean TEST_WHILE_IDLE = false;
	// private static ZkHelp zkHelp = ZkHelp.getInstance();
	/**
	 * 公用redis集群 共享资源池
	 */
	private ShardedJedisPool shardCommonPool = null;

	/**
	 * 获取redis实例地址列表
	 * 
	 * @param path
	 * @return 多个地址用英文下的“,”分割
	 */
	public String getRedisList(String path) {
		// String nutList = DefaultStringUtils.toStr(zkHelp.getValue(path));
		String nutList = redisHostP;
		if (nutList == null || nutList.equals(""))
			throw new RuntimeException("!!! zk redis server" + path + " has not been set !!!");
		return nutList;
	}

	/**
	 * 初始化redis 池
	 * 
	 * @param path
	 */
	public void initialPool(String path) {
		try {
			// 如果不为null 先清理再建立
			if (null != shardCommonPool) {
				shardCommonPool.destroy();
				shardCommonPool = null;
			}
			List<JedisShardInfo> listJedisShardInfo = new ArrayList<JedisShardInfo>();
			String serverString = getRedisList(path);
			logger.info("path={},serverString={}", path, serverString);
			String serverArray[] = serverString.split("[,]");
			JedisShardInfo jsi = null;
			String redisInfo[] = null;
			for (String server : serverArray) {
				redisInfo = server.split("[:]");
				jsi = new JedisShardInfo(redisInfo[0], Integer.parseInt(redisInfo[1].toString()), TIME_OUT);
				if (redisInfo.length == 3) {
					jsi.setPassword(redisInfo[2]);
				}
				listJedisShardInfo.add(jsi);
			}
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			config.setTestWhileIdle(TEST_WHILE_IDLE);
			shardCommonPool = new ShardedJedisPool(config, listJedisShardInfo);
			logger.info("init  " + path + " shardedpool  ok");

		} catch (Exception e) {
			logger.error("initialPool error=" + path, e);
		}
	}

	/**
	 * 从公用redis集群获取resource
	 * 
	 * @return
	 */
	public ShardedJedis getJedis() {
		try {
			if (shardCommonPool != null) {
				ShardedJedis resource = shardCommonPool.getResource();
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("[ShardJedisUtil]->[getJedis] error: ", e);
			return null;
		}
	}

	/**
	 * 归还redis链接
	 * 
	 * @param jedis
	 */
	@SuppressWarnings("deprecation")
	public void returnJedis(final ShardedJedis jedis) {
		if (jedis != null) {
			shardCommonPool.returnResource(jedis);
		}
	}

	/**
	 * 归还坏redis链接
	 * 
	 * @param jedis
	 */
	@SuppressWarnings("deprecation")
	public void returnBrokenJedis(final ShardedJedis jedis) {
		if (jedis != null) {
			shardCommonPool.returnBrokenResource(jedis);
		}
	}

	/**
	 * 内部接口也是回调接口，只定义抽象方法
	 * 
	 * @author haoxw
	 * @since 2014/4/22
	 */
	public interface JedisCallback {
		Object execute(ShardedJedis jedis) throws Exception;
	}

	/**
	 * 具体动作 并自动返回连接 需要具体实现
	 * 
	 * @param callback
	 * @return
	 */
	public Object getResult(JedisCallback callback) {
		ShardedJedis jedis = getJedis();
		try {
			return callback.execute(jedis);
		} catch (Exception e) {
			logger.error("", e);
			returnBrokenJedis(jedis);
			throw new RuntimeException("Redis getResult exception", e);
		} finally {
			if (jedis != null)
				returnJedis(jedis);
		}
	}

	public static void main(String[] args) {

		RedisPool instance = RedisPool.getInstance("redis");

		ShardedJedis dis = instance.getJedis();

		dis.hset("art", "a", "aaa");
		dis.hset("art", "b", "aaba");
		dis.hset("art", "c", "aaada");
		dis.hset("art", "d", "aaac1");

		Map<String, String> value = dis.hgetAll("art");
		for (String string : value.keySet()) {
			System.out.println(value.get(string));

		}

	}
}

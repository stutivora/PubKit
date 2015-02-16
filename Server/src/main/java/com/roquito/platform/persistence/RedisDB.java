package com.roquito.platform.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by puran on 2/6/15.
 */
public final class RedisDB {
    private static final Logger LOG = LoggerFactory.getLogger(RedisDB.class);
    
    private static final RedisDB redisDB = new RedisDB();
    private static final String DEFAULT_HOST = "104.155.200.250";

    private JedisPool jedisPool = null;

    public static RedisDB getInstance() {
        return redisDB;
    }

    public void initRedisDBConnection() {
        jedisPool = new JedisPool(new JedisPoolConfig(), DEFAULT_HOST);
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }

    public static enum Keys {

        KEY_ROQUITO_USERS_COUNT("roquito-users-count");

        private String value;

        Keys(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }
}

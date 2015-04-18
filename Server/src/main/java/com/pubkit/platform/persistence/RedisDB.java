/* Copyright (c) 2015 32skills Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pubkit.platform.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.pubkit.PubKitConfig;

/**
 * Created by puran
 */
@Repository
public class RedisDB {
    private static final Logger LOG = LoggerFactory.getLogger(RedisDB.class);
    
    private JedisPool jedisPool = null;
    private Jedis connection = null;
    
    @Autowired
    public RedisDB(PubKitConfig config) {
        LOG.info("Initializing redis db connection at host {" + config.getRedisHost() + "}");
        if (config.isDevEnvironment()) {
            jedisPool = new JedisPool(new JedisPoolConfig(), config.getRedisHost());
        } else {
            jedisPool = new JedisPool(new JedisPoolConfig(), config.getRedisHost(), config.getRedisPort(),
                    config.getRedisTimeout(), config.getRedisPassword(), config.getRedisDatabase());
        }
        if (jedisPool != null) {
            LOG.info("Connected to redis db at {" + config.getRedisHost() + "}");
        } else {
            LOG.error("Couldn't connect to redis db at {" + config.getRedisHost() + "}");
        }
    }
    
    public Jedis getConnection() {
        connection = jedisPool.getResource();
        return connection;
    }
    
    public void closeConnection() {
        if (connection != null)
            jedisPool.returnResource(connection);
    }
    
    public static enum Keys {
        
        USER_COUNT("user-count"),
        MESSAGE_COUNT("message-count"),
        TOPIC_COUNT("topic-count");
        
        private String value;
        
        Keys(String value) {
            this.value = value;
        }
        
        public String value() {
            return this.value;
        }
    }
}

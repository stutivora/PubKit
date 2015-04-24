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
package com.pubkit.platform.persistence.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.pubkit.PubKitConfig;
import com.pubkit.platform.persistence.RedisDao;

/**
 * Created by puran
 */
@Repository
public class RedisDaoImpl implements RedisDao {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired 
    private PubKitConfig pkConfig;
    
    @Override
    public String getUserAccessToken(String email) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        return ops.get(email);
    }
  
    @Override
    public void saveAccessToken(String email, String accessToken) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        if (pkConfig.getAccessTokenExpirationTime() == 0) {
            ops.set(accessToken, email, 3600, TimeUnit.SECONDS);
        } else {
            ops.set(accessToken, email, pkConfig.getAccessTokenExpirationTime(), TimeUnit.SECONDS);
        }
        ops.set(email, accessToken);
    }
    
    @Override
    public boolean hasAccessToken(String accessToken) {
       return this.redisTemplate.hasKey(accessToken);
    }
    
    @Override
    public void incrementKeyCount(String key) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        ops.increment(key, 1);
    }
}

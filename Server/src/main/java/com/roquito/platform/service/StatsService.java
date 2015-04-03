package com.roquito.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.roquito.platform.persistence.RedisDB;

@Service
public class StatsService {
    
    @Autowired
    private RedisDB redisDB;
    
    public void incrUserCount() {
        redisDB.getConnection().incr(RedisDB.Keys.USER_COUNT.value());
        redisDB.closeConnection();
    }
    
    public void incrtMessageCount() {
        redisDB.getConnection().incr(RedisDB.Keys.MESSAGE_COUNT.value());
        redisDB.closeConnection();
    }
    
    public void incrtTopicCount() {
        redisDB.getConnection().incr(RedisDB.Keys.TOPIC_COUNT.value());
        redisDB.closeConnection();
    }
}

package com.pubkit.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pubkit.platform.persistence.RedisDB;
import com.pubkit.platform.service.StatsService;
/**
 * Created by puran
 */
@Service
public class StatsServiceImpl implements StatsService {
    
    @Autowired
    private RedisDB redisDB;

    @Override
    public void incrUserCount() {
        redisDB.getConnection().incr(RedisDB.Keys.USER_COUNT.value());
        redisDB.closeConnection();
    }

    @Override
    public void incrMessageCount() {
        redisDB.getConnection().incr(RedisDB.Keys.MESSAGE_COUNT.value());
        redisDB.closeConnection(); 
    }

    @Override
    public void incrTopicCount() {
        redisDB.getConnection().incr(RedisDB.Keys.TOPIC_COUNT.value());
        redisDB.closeConnection();
    }
}

package com.roquito.platform.service;

import com.roquito.platform.dao.RedisDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by puran on 2/6/15.
 */
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    public String saveAccessToken(String email, String accessToken) {
        String response = connection().set(accessToken, email, "NX", "EX", 3600);
        log.debug("Access token set response:" + response);
        return response;
    }

    public boolean isAccessTokenValid(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        return connection().get(accessToken) != null;
    }

    public Jedis connection() {
        return RedisDB.getInstance().getConnection();
    }
}

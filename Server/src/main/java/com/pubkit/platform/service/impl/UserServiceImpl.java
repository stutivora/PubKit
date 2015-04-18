package com.pubkit.platform.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pubkit.platform.model.User;
import com.pubkit.platform.persistence.ApplicationDao;
import com.pubkit.platform.persistence.RedisDB;
import com.pubkit.platform.persistence.UserDao;
import com.pubkit.platform.service.UserService;
/**
 * Created by puran
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private ApplicationDao applicationDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private RedisDB redisDB;

    @Override
    public String saveUser(User user) {
        applicationDao.save(user);
        return user.getId().toString();
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findBy("email", email);
    }

    @Override
    public User findByUserId(String userId) {
        return userDao.findBy("userId", userId);
    }

    @Override
    public boolean saveAccessToken(String email, String accessToken) {
        String response = redisDB.getConnection().set(accessToken, email, "NX", "EX", 3600);
        LOG.debug("Access token set response:" + response);
        redisDB.closeConnection();
        
        return "OK".equalsIgnoreCase(response);
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        String response = redisDB.getConnection().get(accessToken);
        redisDB.closeConnection();
        
        return response != null;
    }
}

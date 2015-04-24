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
package com.pubkit.platform.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pubkit.platform.model.User;
import com.pubkit.platform.persistence.ApplicationDao;
import com.pubkit.platform.persistence.RedisDao;
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
    private RedisDao redisDao;

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
    public String getUserAccessToken(String email) {
        return redisDao.getUserAccessToken(email);
    }

    @Override
    public boolean saveAccessToken(String email, String accessToken) {
        redisDao.saveAccessToken(email, accessToken);
        LOG.debug("Access token set saved:" + accessToken);
        
        return true;
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        return redisDao.hasAccessToken(accessToken);
    }
}

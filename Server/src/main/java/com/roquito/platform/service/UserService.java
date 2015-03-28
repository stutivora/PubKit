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
package com.roquito.platform.service;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roquito.platform.model.User;
import com.roquito.platform.persistence.MongoDB;
import com.roquito.platform.persistence.RedisDB;

/**
 * Created by puran
 */
@Service
public class UserService extends BasicDAO<User, String> {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    
    private MongoDB mongoDB;
    private RedisDB redisDB;
    
    @Autowired
    public UserService(MongoDB mongoDB, RedisDB redisDB) {
        this(mongoDB.getDataStore());
        
        this.mongoDB = mongoDB;
        this.redisDB = redisDB;
    }
    
    private UserService(Datastore ds) {
        super(ds);
    }
    
    public String saveUser(User user) {
        if (user == null) {
            return null;
        }
        Key<User> userKey = this.save(user);
        ObjectId userId = (ObjectId) userKey.getId();
        
        return userId.toString();
    }
    
    public User findByEmail(String email) {
        return this.findOne("email", email);
    }
    
    public User findByUserId(String userId) {
        return this.findOne("userId", userId);
    }
    
    public boolean saveAccessToken(String email, String accessToken) {
        String response = redisDB.getConnection().set(accessToken, email, "NX", "EX", 3600);
        LOG.debug("Access token set response:" + response);
        redisDB.closeConnection();
        
        return "OK".equalsIgnoreCase(response);
    }
    
    public boolean isAccessTokenValid(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        String response = redisDB.getConnection().get(accessToken);
        redisDB.closeConnection();
        
        return response != null;
    }
    
    public String getNextUserId() {
        Long nextId = mongoDB.generateNextId("com.roquito.platform.model.User");
        return nextId.toString();
    }
}

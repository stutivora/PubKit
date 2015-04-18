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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.pubkit.platform.model.DeviceInfo;
import com.pubkit.platform.persistence.DeviceInfoDao;

/**
 * Created by puran
 */
@Repository 
public class DeviceInfoDaoImpl implements DeviceInfoDao {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void save(Object object) {
        mongoTemplate.save(object);
    }
    
    public DeviceInfo getDeviceInfoForRegistrationId(String applicationId, String registrationId) {
        Criteria criteria = Criteria.where("applicationId").is(applicationId).and("registrationId").is(registrationId);
        Query query = Query.query(criteria);
        
        return mongoTemplate.findOne(query, DeviceInfo.class);
    }
    
    public DeviceInfo getDeviceInfoForToken(String applicationId, String deviceToken) {
        Criteria criteria = Criteria.where("applicationId").is(applicationId).and("deviceToken").is(deviceToken);
        Query query = Query.query(criteria);
        
        return mongoTemplate.findOne(query, DeviceInfo.class);
    }
    
    public List<DeviceInfo> getDeviceInfoForUserId(String applicationId, String userId, String deviceType) {
        Criteria criteria = Criteria.where("applicationId").is(applicationId).
                and("userId").is(userId).and("deviceType").is(deviceType);
        Query query = Query.query(criteria);
        
        return mongoTemplate.find(query, DeviceInfo.class);
    }
    
    public List<DeviceInfo> getDevices(String applicationId, String deviceType, int offset, int limit) {
        Criteria criteria = Criteria.where("applicationId").is(applicationId).and("deviceType").is(deviceType);
        Query query = Query.query(criteria).limit(limit).skip(offset);
        
        return mongoTemplate.find(query, DeviceInfo.class);
    }

    @Override
    public DeviceInfo getDeviceInfo(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), DeviceInfo.class);
    }
}

package com.roquito.platform.service;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roquito.platform.model.DeviceInfo;
import com.roquito.platform.persistence.MongoDB;

@Service
public class DeviceInfoService extends BasicDAO<DeviceInfo, String> {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceInfoService.class);
    
    private MongoDB mongoDB;
    
    @Autowired
    public DeviceInfoService(MongoDB mongoDB) {
        this(mongoDB.getDataStore());
        this.mongoDB = mongoDB;
    }
    
    private DeviceInfoService(Datastore ds) {
        super(ds);
    }
    
    public String saveDeviceInfo(DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return null;
        }
        Key<DeviceInfo> applicationKey = this.save(deviceInfo);
        ObjectId objectId = (ObjectId) applicationKey.getId();
        
        return objectId.toString();
    }
    
    public DeviceInfo getDeviceInfoForRegistrationId(String applicationId, String registrationId) {
        Query<DeviceInfo> query = mongoDB.getDataStore().createQuery(DeviceInfo.class).field("applicationId")
                .equal(applicationId).field("registrationId").equal(registrationId);
        
        QueryResults<DeviceInfo> results = this.find(query);
        if (results != null) {
            return results.get();
        } else {
            LOG.info("App device not found for registration id:" + registrationId);
            return null;
        }
    }
    
    public DeviceInfo getDeviceInfoForToken(String applicationId, String deviceToken) {
        Query<DeviceInfo> query = mongoDB.getDataStore().createQuery(DeviceInfo.class).field("applicationId")
                .equal(applicationId).field("deviceToken").equal(deviceToken);
        
        QueryResults<DeviceInfo> results = this.find(query);
        if (results != null) {
            return results.get();
        } else {
            LOG.info("App device not found for device token:" + deviceToken);
            return null;
        }
    }
}

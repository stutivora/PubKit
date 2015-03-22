package com.roquito.platform.service;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roquito.platform.model.AppDevice;
import com.roquito.platform.persistence.MongoDB;

@Service
public class AppDeviceService extends BasicDAO<AppDevice, String> {
    
    private MongoDB mongoDB;
    
    @Autowired
    public AppDeviceService(MongoDB mongoDB) {
        this(mongoDB.getDataStore());
        this.mongoDB = mongoDB;
    }
    
    private AppDeviceService(Datastore ds) {
        super(ds);
    }
    
    public String saveAppDevice(AppDevice appDevice) {
        if (appDevice == null) {
            return null;
        }
        Key<AppDevice> applicationKey = this.save(appDevice);
        ObjectId objectId = (ObjectId) applicationKey.getId();
        
        return objectId.toString();
    }
    
    public AppDevice getAppDeviceForToken(String applicationId, String deviceToken) {
        Query<AppDevice> query = mongoDB.getDataStore().createQuery(AppDevice.class).field("applicationId")
                .equal(applicationId).field(deviceToken).equal(deviceToken);
        
        QueryResults<AppDevice> results = this.find(query);
        if (results != null) {
            return results.get();
        } else {
            return null;
        }
    }
}

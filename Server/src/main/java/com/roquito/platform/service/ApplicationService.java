package com.roquito.platform.service;

import com.roquito.platform.model.Application;
import com.roquito.platform.persistence.MongoDB;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * Created by puran on 1/31/15.
 */
public class ApplicationService extends BasicDAO<Application, String> {

    public ApplicationService() {
        this(MongoDB.getInstance().getDataStore());
    }

    private ApplicationService(Datastore ds) {
        super(ds);
    }

    public Application findByApplicationId(String applicationId) {
        return this.findOne("applicationId", applicationId);
    }

    public Application findByApplicationName(String applicationName) {
        return this.findOne("applicationName", applicationName);
    }

    public String saveApplication(Application application) {
        if (application == null) {
            return null;
        }
        Key<Application> applicationKey = this.save(application);
        ObjectId objectId = (ObjectId) applicationKey.getId();

        return objectId.toString();
    }

    public String getNextApplicationId() {
        Long nextId = MongoDB.getInstance().generateNextId("com.roquito.platform.model.Application");
        return nextId.toString();
    }
}

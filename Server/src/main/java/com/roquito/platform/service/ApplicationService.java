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

import java.util.Collections;
import java.util.List;

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

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.roquito.platform.model.Application;
import com.roquito.platform.model.User;
import com.roquito.platform.persistence.MongoDB;

/**
 * Created by puran
 */
@Service
public class ApplicationService extends BasicDAO<Application, String> {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);
    
    private MongoDB mongoDB;
    
    @Autowired
    public ApplicationService(MongoDB mongoDB) {
        this(mongoDB.getDataStore());
        this.mongoDB = mongoDB;
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
        LOG.info("Saved new application");
        
        return objectId.toString();
    }
    
    public List<Application> getUserApplications(User user) {
        Query<Application> query = mongoDB.getDataStore().createQuery(Application.class).field("owner").equal(user).order("createdDate");
        QueryResults<Application> results = this.find(query);
        if (results != null) {
            return results.asList();
        } else {
            return Collections.emptyList();
        }
    }
    
    public String saveFile(byte[] fileData, String fileName) {
        GridFS gridFs = new GridFS(mongoDB.getDataStore().getDB(), "roquito");
        GridFSInputFile gfsFile = gridFs.createFile(fileData);
        
        gfsFile.setFilename(fileName);
        gfsFile.save();
        LOG.info("Saved new file :"+fileName);
        
        return gfsFile.getId().toString();
    }
    
    public String getNextApplicationId() {
        Long nextId = mongoDB.generateNextId("com.roquito.platform.model.Application");
        return nextId.toString();
    }
}

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

import java.io.InputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.pubkit.platform.model.Application;
import com.pubkit.platform.model.User;
import com.pubkit.platform.persistence.ApplicationDao;
/**
 * Created by puran
 */
@Repository
public class ApplicationDaoImpl implements ApplicationDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDaoImpl.class);
    
    private static final String PK_FILES_BUCKET = "pkfiles";
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Application findByApplicationId(String applicationId) {
        Criteria criteria = Criteria.where("applicationId").is(applicationId);
        return mongoTemplate.findOne(Query.query(criteria), Application.class);
    }  
    
    public Application findByApplicationName(String applicationName) {
        Criteria criteria = Criteria.where("applicationName").is(applicationName);
        return mongoTemplate.findOne(Query.query(criteria), Application.class);
    }
    
    public void save(Object object) {
        mongoTemplate.save(object);
    }
    
    public List<Application> getUserApplications(User user) {
        Criteria criteria = Criteria.where("owner").is(user);
        
        Query query = Query.query(criteria);
        
        Sort sort = new Sort(new Order(Direction.ASC, "createdDate"));
        query = query.with(sort);
        
        return mongoTemplate.find(query, Application.class);
    }
    
    public String saveFile(byte[] fileData, String fileName, String contentType) {
        GridFS gridFs = new GridFS(mongoTemplate.getDb(), PK_FILES_BUCKET);
        GridFSInputFile gfsFile = gridFs.createFile(fileData);
        
        gfsFile.setFilename(fileName);
        gfsFile.setContentType(contentType);
        gfsFile.save();
        
        LOG.info("Saved new file :" + fileName);
        
        return gfsFile.getId().toString();
    }
    
    public InputStream getFileAsStream(String fileId) {
        GridFS gridFs = new GridFS(mongoTemplate.getDb(), PK_FILES_BUCKET);
        GridFSDBFile savedFile = gridFs.find(new ObjectId(fileId));
        
        return savedFile.getInputStream();
    }
    
    public GridFSDBFile getFile(String fileId) {
        GridFS gridFs = new GridFS(mongoTemplate.getDb(), PK_FILES_BUCKET);
        return gridFs.find(new ObjectId(fileId));
    }
}

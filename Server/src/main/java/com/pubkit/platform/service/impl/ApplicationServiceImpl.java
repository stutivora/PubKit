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

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;
import com.pubkit.platform.model.Application;
import com.pubkit.platform.model.User;
import com.pubkit.platform.persistence.ApplicationDao;
import com.pubkit.platform.persistence.SequenceDao;
import com.pubkit.platform.service.ApplicationService;
/**
 * Created by puran
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);
    
    @Autowired
    private ApplicationDao applicationDao;
    
    @Autowired 
    private SequenceDao sequenceDao;
     
    public Application findByApplicationId(String applicationId) {
        return applicationDao.findByApplicationId(applicationId);
    }
    
    public Application findByApplicationName(String applicationName) {
        return applicationDao.findByApplicationName(applicationName);
    }
    
    public void saveApplication(Application application) {
        if (application != null) {
            applicationDao.save(application);
            LOG.info("Saved new application");
        }
    }
    
    public List<Application> getUserApplications(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        List<Application> results = applicationDao.getUserApplications(user);
        if (results != null) {
            return results;
        } else {
            return Collections.emptyList();
        }
    }
    
    public String saveFile(byte[] fileData, String fileName, String contentType) {
        return applicationDao.saveFile(fileData, fileName, contentType);
    }
    
    public InputStream getFileAsStream(String fileId) {
        return applicationDao.getFileAsStream(fileId);
    }
    
    public GridFSDBFile getFile(String fileId) {
        return applicationDao.getFile(fileId);
    }
    
    public String getNextId(Class<?> cls) {
        long nextId = sequenceDao.getNextSequenceId(cls.getName());
        
        return String.valueOf(nextId);
    }
}

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
package com.pubkit.platform.service;

import java.io.InputStream;
import java.util.List;

import com.mongodb.gridfs.GridFSDBFile;
import com.pubkit.platform.model.Application;
import com.pubkit.platform.model.User;

/**
 * Created by puran
 */
public interface ApplicationService {
    
    void saveApplication(Application application);
    
    Application findByApplicationId(String applicationId);
    
    Application findByApplicationName(String applicationName);
    
    List<Application> getUserApplications(User user);
    
    String saveFile(byte[] fileData, String fileName, String contentType);
    
    InputStream getFileAsStream(String fileId);
    
    GridFSDBFile getFile(String fileId);
    
    String getNextId(Class<?> cls);
}

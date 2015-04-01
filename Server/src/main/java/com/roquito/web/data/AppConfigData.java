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
package com.roquito.web.data;

/**
 * Created by puran
 */
public class AppConfigData {
    private String applicationId;
    private String androidGCMKey;
    private String apnsDevCertFileId;
    private String apnsDevCertFileName;
    private String apnsDevCertPassword;
    private String apnsProdCertFileId;
    private String apnsProdCertFileName;
    private String apnsProdCertPassword;
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getAndroidGCMKey() {
        return androidGCMKey;
    }
    
    public void setAndroidGCMKey(String androidGCMKey) {
        this.androidGCMKey = androidGCMKey;
    }
    
    public String getApnsDevCertFileId() {
        return apnsDevCertFileId;
    }
    
    public void setApnsDevCertFileId(String apnsDevCertFileId) {
        this.apnsDevCertFileId = apnsDevCertFileId;
    }
    
    public String getApnsDevCertFileName() {
        return apnsDevCertFileName;
    }
    
    public void setApnsDevCertFileName(String apnsDevCertFileName) {
        this.apnsDevCertFileName = apnsDevCertFileName;
    }
    
    public String getApnsDevCertPassword() {
        return apnsDevCertPassword;
    }
    
    public void setApnsDevCertPassword(String apnsDevCertPassword) {
        this.apnsDevCertPassword = apnsDevCertPassword;
    }
    
    public String getApnsProdCertFileId() {
        return apnsProdCertFileId;
    }
    
    public void setApnsProdCertFileId(String apnsProdCertFileId) {
        this.apnsProdCertFileId = apnsProdCertFileId;
    }
    
    public String getApnsProdCertFileName() {
        return apnsProdCertFileName;
    }
    
    public void setApnsProdCertFileName(String apnsProdCertFileName) {
        this.apnsProdCertFileName = apnsProdCertFileName;
    }
    
    public String getApnsProdCertPassword() {
        return apnsProdCertPassword;
    }
    
    public void setApnsProdCertPassword(String apnsProdCertPassword) {
        this.apnsProdCertPassword = apnsProdCertPassword;
    }
    
}

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
package com.roquito.web.dto;

/**
 * Created by puran
 */
public class AppConfigDto {
    private String applicationId;
    private String type;
    private String androidGCMKey;
    private String apnsDevCertFilePath;
    private String apnsDevCertPassword;
    private String apnsProdCertFilePath;
    private String apnsProdCertPassword;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAndroidGCMKey() {
        return androidGCMKey;
    }

    public void setAndroidGCMKey(String androidGCMKey) {
        this.androidGCMKey = androidGCMKey;
    }

    public String getApnsDevCertFilePath() {
        return apnsDevCertFilePath;
    }

    public void setApnsDevCertFilePath(String apnsDevCertFilePath) {
        this.apnsDevCertFilePath = apnsDevCertFilePath;
    }

    public String getApnsDevCertPassword() {
        return apnsDevCertPassword;
    }

    public void setApnsDevCertPassword(String apnsDevCertPassword) {
        this.apnsDevCertPassword = apnsDevCertPassword;
    }

    public String getApnsProdCertFilePath() {
        return apnsProdCertFilePath;
    }

    public void setApnsProdCertFilePath(String apnsProdCertFilePath) {
        this.apnsProdCertFilePath = apnsProdCertFilePath;
    }

    public String getApnsProdCertPassword() {
        return apnsProdCertPassword;
    }

    public void setApnsProdCertPassword(String apnsProdCertPassword) {
        this.apnsProdCertPassword = apnsProdCertPassword;
    }
}

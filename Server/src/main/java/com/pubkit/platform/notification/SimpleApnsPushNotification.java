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
package com.pubkit.platform.notification;

import java.util.List;

/**
 * Created by puran
 */
public class SimpleApnsPushNotification implements PushNotification {
    private String applicationId;
    private String applicationVersion;
    private String alert;
    private int badge;
    private String sound;
    private int contentAvailable;
    private List<String> deviceTokens;
    private boolean productionMode;
    private boolean needFeedback;
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getApplicationVersion() {
        return applicationVersion;
    }
    
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
    
    public String getAlert() {
        return alert;
    }
    
    public void setAlert(String alert) {
        this.alert = alert;
    }
    
    public int getBadge() {
        return badge;
    }
    
    public void setBadge(int badge) {
        this.badge = badge;
    }
    
    public String getSound() {
        return sound;
    }
    
    public void setSound(String sound) {
        this.sound = sound;
    }
    
    public int getContentAvailable() {
        return contentAvailable;
    }
    
    public void setContentAvailable(int contentAvailable) {
        this.contentAvailable = contentAvailable;
    }
    
    public List<String> getDeviceTokens() {
        return deviceTokens;
    }
    
    public void setDeviceTokens(List<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
    }
    
    public boolean isProductionMode() {
        return productionMode;
    }
    
    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }
    
    public boolean isNeedFeedback() {
        return needFeedback;
    }
    
    public void setNeedFeedback(boolean needFeedback) {
        this.needFeedback = needFeedback;
    }
}

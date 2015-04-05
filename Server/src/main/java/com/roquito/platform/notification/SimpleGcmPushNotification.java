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
package com.roquito.platform.notification;

import java.util.List;
import java.util.Map;

/**
 * Gcm notification object containing parameters defined by GCM API reference guide
 * {@link https://developer.android.com/google/gcm/server-ref.html}
 * 
 * Created by puran 
 */
public class SimpleGcmPushNotification implements PushNotification {
    /* PubKit application id */
    private String applicationId;
    /* PubKit application version */
    private String applicationVersion;
    /* It must contain at least 1 and at most 1000 registration IDs.*/
    private List<String> registrationIds;
    /* Optional, This parameters identifies a group of messages */
    private String collapseKey;
    /* Optional, default is false. */
    private boolean delayWhileIdle;
    /* The default value is 4 weeks. */
    private int timeToLive;
    /* Key value pair of push data for the app, eg: {"score":"3x1"} */
    private Map<String, String> data;
    private boolean retry;
    private boolean multicast;
    
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
    
    public String getCollapseKey() {
        return collapseKey;
    }
    
    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }
    
    public boolean isDelayWhileIdle() {
        return delayWhileIdle;
    }
    
    public void setDelayWhileIdle(boolean delayWhileIdle) {
        this.delayWhileIdle = delayWhileIdle;
    }
    
    public int getTimeToLive() {
        return timeToLive;
    }
    
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    public Map<String, String> getData() {
        return data;
    }
    
    public void setData(Map<String, String> data) {
        this.data = data;
    }
    
    public boolean isRetry() {
        return retry;
    }
    
    public void setRetry(boolean retry) {
        this.retry = retry;
    }
    
    public boolean isMulticast() {
        return multicast;
    }
    
    public void setMulticast(boolean multicast) {
        this.multicast = multicast;
    }
    
    public List<String> getRegistrationIds() {
        return registrationIds;
    }
    
    public void setRegistrationIds(List<String> registrationIds) {
        this.registrationIds = registrationIds;
    }
}

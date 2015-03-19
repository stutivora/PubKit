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
package com.roquito.web.request;

import java.util.Map;

/**
 * Created by puran
 */
public class ApplicationRequest {
    private String applicationId;
    private String applicationName;
    private String applicationDescription;
    private String userId;
    private String applicationKey;
    private String applicationSecret;
    private String websiteLink;
    private String pricingPlan;
    private Map<String, String> configParams;

    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getApplicationKey() {
        return applicationKey;
    }
    
    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }
    
    public String getApplicationDescription() {
        return applicationDescription;
    }
    
    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }
    
    public String getApplicationSecret() {
        return applicationSecret;
    }
    
    public void setApplicationSecret(String applicationSecret) {
        this.applicationSecret = applicationSecret;
    }
    
    public String getWebsiteLink() {
        return websiteLink;
    }
    
    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }
    
    public String getPricingPlan() {
        return pricingPlan;
    }
    
    public void setPricingPlan(String pricingPlan) {
        this.pricingPlan = pricingPlan;
    }

    public Map<String, String> getConfigParams() {
        return configParams;
    }

    public void setConfigParams(Map<String, String> configParams) {
        this.configParams = configParams;
    }
}

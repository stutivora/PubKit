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
package com.roquito.platform.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by puran
 */
@Entity
public class Application {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String applicationId;
    @Indexed(unique = true)
    private String applicationKey;
    @Indexed(unique = true)
    private String applicationSecret;
    @Indexed(unique = true, dropDups = true)
    private String applicationName;
    private String applicationDescription;
    private String websiteLink;
    private String pricingPlan;
    @Reference
    private User owner;
    @Embedded
    private List<ApplicationUser> applicationUsers;
    @Embedded
    private List<ApplicationConfig> applicationConfigs;
    private Date createdDate;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getPricingPlan() {
        return pricingPlan;
    }

    public void setPricingPlan(String pricingPlan) {
        this.pricingPlan = pricingPlan;
    }

    public List<ApplicationUser> getApplicationUsers() {
        return applicationUsers;
    }

    public void setApplicationUsers(List<ApplicationUser> applicationUsers) {
        this.applicationUsers = applicationUsers;
    }

    public List<ApplicationConfig> getApplicationConfigs() {
        return applicationConfigs;
    }

    public void setApplicationConfigs(List<ApplicationConfig> applicationConfigs) {
        this.applicationConfigs = applicationConfigs;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getAndroidGCMKey() {
        List<ApplicationConfig> appConfigs = this.getApplicationConfigs();
        for (ApplicationConfig appConfig : appConfigs) {
            if (DataConstants.TYPE_ANDROID.equals(appConfig.getType())) {
                Map<String, String> params = appConfig.getConfigParams();
                if (params != null) {
                    return params.get(DataConstants.ANDROID_GCM_KEY);
                }
            }
        }
        return null;
    }
}

package com.pubkit.platform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DeviceInfo {
    @Id
    private String id;
    @Indexed(unique = true)
    private String applicationId;
    @Indexed(unique = true)
    private String deviceToken;
    @Indexed(unique = true)
    private String registrationId;
    @Indexed(unique = true)
    private String sourceUserId;
    private String deviceType;
    private String deviceSubType;
    private boolean active;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getDeviceToken() {
        return deviceToken;
    }
    
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
    
    public String getRegistrationId() {
        return registrationId;
    }
    
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
    
    public String getSourceUserId() {
        return sourceUserId;
    }
    
    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getDeviceSubType() {
        return deviceSubType;
    }
    
    public void setDeviceSubType(String deviceSubType) {
        this.deviceSubType = deviceSubType;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}

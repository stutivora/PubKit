package com.pubkit.model;

/**
 * Created by puran on 3/25/15.
 */
public final class PubKitRegistrationInfo {
    private String applicationId;
    private String deviceAppId;
    private String registrationId;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDeviceAppId() {
        return deviceAppId;
    }

    public void setDeviceAppId(String deviceAppId) {
        this.deviceAppId = deviceAppId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}

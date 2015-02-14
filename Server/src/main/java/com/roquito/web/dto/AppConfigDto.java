package com.roquito.web.dto;

/**
 * Created by puran on 2/7/15.
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

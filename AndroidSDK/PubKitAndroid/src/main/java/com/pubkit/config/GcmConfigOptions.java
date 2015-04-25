package com.pubkit.config;

/**
 * Class to hold all the configuration for GCM messaing in order to receive push notification.
 * For creating senderId and GCM API_KEY follow {@link http://developer.android.com/google/gcm/gs.html}
 * <p/>
 * Created by puran on 4/10/15.
 */
public final class GcmConfigOptions {
    /*
     * User id is the unique identifier of the user in the client application. It has to be unique
     * within the app.
     */
    private String userId;
    private String senderId;
    private String gcmApiKey;

    public GcmConfigOptions(String userId, String senderId, String gcmApiKey) {
        this.userId = userId;
        this.senderId = senderId;
        this.gcmApiKey = gcmApiKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getGcmApiKey() {
        return gcmApiKey;
    }

    public void setGcmApiKey(String gcmApiKey) {
        this.gcmApiKey = gcmApiKey;
    }
}

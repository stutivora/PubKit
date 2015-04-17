package com.pubkit.config;

/**
 * Class that holds the credential info to connect to PubKit backend. Credentials can be obtained
 * from {@link http://pubkit.co} by registering an app.
 *
 * Created by puran on 4/10/15.
 */
public final class PubKitCredentials {

    private String pubKitAppId;
    private String pubKitApiKey;
    private String pubKitApiSecret;

    public PubKitCredentials(String pubKitAppId, String pubKitApiKey, String pubKitApiSecret) {
        this.pubKitAppId = pubKitAppId;
        this.pubKitApiKey = pubKitApiKey;
        this.pubKitApiSecret = pubKitApiSecret;
    }

    public String getPubKitAppId() {
        return pubKitAppId;
    }

    public void setPubKitAppId(String pubKitAppId) {
        this.pubKitAppId = pubKitAppId;
    }

    public String getPubKitApiKey() {
        return pubKitApiKey;
    }

    public void setPubKitApiKey(String pubKitApiKey) {
        this.pubKitApiKey = pubKitApiKey;
    }

    public String getPubKitApiSecret() {
        return pubKitApiSecret;
    }

    public void setPubKitApiSecret(String pubKitApiSecret) {
        this.pubKitApiSecret = pubKitApiSecret;
    }
}

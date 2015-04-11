package com.roquito.platform.messaging.protocol.pkmp.proto;

public class PKMPConnect extends PKMPBasePayload {
    
    private static final String API_KEY = "api_key";
    private static final String API_VERSION = "api_version";
    
    public PKMPConnect(PKMPPayload pKMPPayload) {
        super(pKMPPayload);
    }
    
    public String getApiKey() {
        return getHeaders().get(API_KEY);
    }
    
    public String getApiVersion() {
        return getHeaders().get(API_VERSION);
    }
}

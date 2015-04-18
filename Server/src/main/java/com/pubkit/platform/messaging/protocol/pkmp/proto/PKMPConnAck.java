package com.pubkit.platform.messaging.protocol.pkmp.proto;

public class PKMPConnAck extends PKMPBasePayload {
    
    public static final String APPLICATION_INVALID = "APPLICATION_INVALID";
    public static final String CONNECTION_FAILED = "CONNECTION_FAILED";
    
    public PKMPConnAck(String clientId, String sessionId, String accessToken) {
        super(clientId);
        addHeader(SESSION_ID, sessionId);
        addHeader(SESSION_TOKEN, accessToken);
        addHeader(TYPE, CONNACK);
    }
    
    public PKMPConnAck(String clientId, String errorStatus) {
        super(clientId);
        
        addHeader(STATUS, errorStatus);
        addHeader(TYPE, CONNACK);
    }
}

package com.roquito.platform.messaging.protocol;

public class ConnAck extends Payload {
    
    public static final String APPLICATION_INVALID = "APPLICATION_INVALID";
    public static final String CONNECTION_FAILED = "CONNECTION_FAILED";
    
    public ConnAck(String clientId, String sessionId, String accessToken) {
        super(clientId);
        addHeader(SESSION_ID, sessionId);
        addHeader(SESSION_TOKEN, accessToken);
        addHeader(TYPE, CONNACK);
    }
    
    public ConnAck(String clientId, String errorStatus) {
        super(clientId);
        
        addHeader(STATUS, errorStatus);
        addHeader(TYPE, CONNACK);
    }
}

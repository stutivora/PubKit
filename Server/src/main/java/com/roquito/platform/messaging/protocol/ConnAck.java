package com.roquito.platform.messaging.protocol;

public class ConnAck extends Payload {
    
    public ConnAck(String sessionId, String accessToken) {
        addHeader(TYPE, CONNACK);
        addHeader(SESSION_ID, sessionId);
        addHeader(SESSION_TOKEN, accessToken);
    }
}

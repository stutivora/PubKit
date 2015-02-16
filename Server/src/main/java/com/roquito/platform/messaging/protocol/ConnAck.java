package com.roquito.platform.messaging.protocol;

public class ConnAck extends Payload {
    
    public ConnAck(String sessionId, String accessToken) {
	addHeader(SESSION_ID, sessionId);
	addHeader(SESSION_TOKEN, accessToken);
    }
}

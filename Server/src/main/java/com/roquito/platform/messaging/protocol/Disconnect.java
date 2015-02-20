package com.roquito.platform.messaging.protocol;

public class Disconnect extends Payload {
    
    public Disconnect(String clientId) {
	addHeader(TYPE, DISCONNECT);
	addHeader(CLIENT_ID, clientId);
    }
}

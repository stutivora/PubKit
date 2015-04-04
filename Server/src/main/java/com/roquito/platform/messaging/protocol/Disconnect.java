package com.roquito.platform.messaging.protocol;

public class Disconnect extends BasePayload {
    
    public Disconnect(String clientId) {
        super(clientId);
        addHeader(TYPE, DISCONNECT);
    }
}

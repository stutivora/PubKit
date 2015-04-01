package com.roquito.platform.messaging.protocol;

public class PubAck extends Payload {
    
    public PubAck(String clientId) {
        super(clientId);
        addHeader(TYPE, SUBSACK);
    }
}

package com.roquito.platform.messaging.protocol;

public class SubsAck extends Payload {
    
    public SubsAck(String clientId) {
        super(clientId);
        addHeader(TYPE, SUBSACK);
    }
}

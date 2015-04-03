package com.roquito.platform.messaging.protocol;

public class PubAck extends Payload {
    
    public PubAck(String clientId, String topic) {
        super(clientId);
        
        addHeader(TOPIC, topic);
        addHeader(TYPE, PUBACK);
    }
    
    public PubAck(String clientId, String topic, String errorStatus) {
        this(clientId, topic);
        addHeader(STATUS, errorStatus);
    }
}

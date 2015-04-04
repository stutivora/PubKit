package com.roquito.platform.messaging.protocol;

public class UnSubsAck extends BasePayload {
    
    public UnSubsAck(String clientId, String topic) {
        super(clientId);
        addHeader(TOPIC, topic);
        addHeader(TYPE, UNSUBSACK);
    }
    
    public UnSubsAck(String clientId, String topic, String errorStatus) {
        this(clientId, topic);
        addHeader(STATUS, errorStatus);
    }
}

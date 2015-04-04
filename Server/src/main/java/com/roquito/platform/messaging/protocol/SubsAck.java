package com.roquito.platform.messaging.protocol;

public class SubsAck extends BasePayload {
    
    public SubsAck(String clientId, String topic) {
        super(clientId);
        addHeader(TOPIC, topic);
        addHeader(TYPE, SUBSACK);
    }
    
    public SubsAck(String clientId, String topic, String errorStatus) {
        this(clientId, topic);
        addHeader(STATUS, errorStatus);
    }
}

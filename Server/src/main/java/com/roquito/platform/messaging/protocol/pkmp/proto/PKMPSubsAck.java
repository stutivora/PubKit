package com.roquito.platform.messaging.protocol.pkmp.proto;

public class PKMPSubsAck extends PKMPBasePayload {
    
    public PKMPSubsAck(String clientId, String topic) {
        super(clientId);
        addHeader(TOPIC, topic);
        addHeader(TYPE, SUBSACK);
    }
    
    public PKMPSubsAck(String clientId, String topic, String errorStatus) {
        this(clientId, topic);
        addHeader(STATUS, errorStatus);
    }
}

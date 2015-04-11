package com.roquito.platform.messaging.protocol.pkmp.proto;

public class PKMPUnSubsAck extends PKMPBasePayload {
    
    public PKMPUnSubsAck(String clientId, String topic) {
        super(clientId);
        addHeader(TOPIC, topic);
        addHeader(TYPE, UNSUBSACK);
    }
    
    public PKMPUnSubsAck(String clientId, String topic, String errorStatus) {
        this(clientId, topic);
        addHeader(STATUS, errorStatus);
    }
}

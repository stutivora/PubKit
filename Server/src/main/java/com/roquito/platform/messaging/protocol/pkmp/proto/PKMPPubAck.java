package com.roquito.platform.messaging.protocol.pkmp.proto;

public class PKMPPubAck extends PKMPBasePayload {
    
    public PKMPPubAck(String clientId, String topic) {
        super(clientId);
        
        addHeader(TOPIC, topic);
        addHeader(TYPE, PUBACK);
    }
    
    public PKMPPubAck(String clientId, String topic, String errorStatus) {
        this(clientId, topic);
        addHeader(STATUS, errorStatus);
    }
}

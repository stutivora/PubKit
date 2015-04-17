package com.roquito.platform.messaging.protocol.pkmp.proto;

public class PKMPPubMessage extends PKMPBasePayload {
    
    public PKMPPubMessage(String clientId, String senderId) {
        super(clientId);
        addHeader(TYPE, MESSAGE);
        addHeader(SENDER_ID, senderId);
    }
    
    public void setTopic(String topic) {
        addHeader(TOPIC, topic);
    }
    
    public void setMessageId(String messageId) {
        addHeader(MESSAGE_ID, messageId);
    }
}

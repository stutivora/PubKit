package com.roquito.platform.messaging.protocol;

public class PubMessage extends Payload {
    
    public PubMessage(String clientId, String senderId) {
        super(clientId);
        addHeader(TYPE, MESSAGE);
        addHeader(SENDER_ID, senderId);
    }
    
    public void setTopic(String topic) {
        addHeader(TOPIC, topic);
    }
}

package com.roquito.platform.messaging.protocol;

public class Subscribe extends Payload {
    
    public String getTopic() {
        return getHeader(TOPIC);
    }
}

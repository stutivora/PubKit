package com.pubkit.platform.persistence;

public enum RedisKeys {
    
    USER_COUNT("user-count"), MESSAGE_COUNT("message-count"), TOPIC_COUNT("topic-count");
    
    private String value;
    
    RedisKeys(String value) {
        this.value = value;
    }
    
    public String value() {
        return this.value;
    }
}

package com.roquito.platform.messaging.protocol;

public interface Payload {

    String getType();
    
    String getClientId();
    
    void setData(String data);
    
    void addHeader(String header, String value);
}

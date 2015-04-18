package com.pubkit.platform.messaging.protocol.pkmp.proto;

public interface PKMPPayload {

    String getType();
    
    String getClientId();
    
    void setData(String data);
    
    void addHeader(String header, String value);
}

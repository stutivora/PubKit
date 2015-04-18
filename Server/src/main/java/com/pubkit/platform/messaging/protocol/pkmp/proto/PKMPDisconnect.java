package com.pubkit.platform.messaging.protocol.pkmp.proto;

public class PKMPDisconnect extends PKMPBasePayload {
    
    public PKMPDisconnect(String clientId) {
        super(clientId);
        addHeader(TYPE, DISCONNECT);
    }
}

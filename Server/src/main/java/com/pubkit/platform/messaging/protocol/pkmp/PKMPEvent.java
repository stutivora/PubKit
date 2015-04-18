package com.pubkit.platform.messaging.protocol.pkmp;

import org.springframework.web.socket.WebSocketSession;

import com.lmax.disruptor.EventFactory;
import com.pubkit.platform.messaging.protocol.pkmp.proto.PKMPPayload;

public class PKMPEvent {
    
    private long sequence;
    private PKMPPayload pKMPPayload;
    private WebSocketSession session;
    
    public long getSequence() {
        return sequence;
    }
    
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
    
    public PKMPPayload getPayload() {
        return pKMPPayload;
    }
    
    public void setPayload(PKMPPayload pKMPPayload) {
        this.pKMPPayload = pKMPPayload;
    }
    
    public WebSocketSession getSession() {
        return session;
    }
    
    public void setSession(WebSocketSession session) {
        this.session = session;
    }
    
    public final static EventFactory<PKMPEvent> EVENT_FACTORY = new EventFactory<PKMPEvent>() {
        public PKMPEvent newInstance() {
            return new PKMPEvent();
        }
    };
}

package com.roquito.platform.messaging;

import org.springframework.web.socket.WebSocketSession;

import com.lmax.disruptor.EventFactory;
import com.roquito.platform.messaging.protocol.Payload;

public class MessagingEvent {
    
    private long sequence;
    private Payload payload;
    private WebSocketSession session;
    
    public long getSequence() {
        return sequence;
    }
    
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
    
    public Payload getPayload() {
        return payload;
    }
    
    public void setPayload(Payload payload) {
        this.payload = payload;
    }
    
    public WebSocketSession getSession() {
        return session;
    }
    
    public void setSession(WebSocketSession session) {
        this.session = session;
    }
    
    public final static EventFactory<MessagingEvent> EVENT_FACTORY = new EventFactory<MessagingEvent>() {
        public MessagingEvent newInstance() {
            return new MessagingEvent();
        }
    };
}

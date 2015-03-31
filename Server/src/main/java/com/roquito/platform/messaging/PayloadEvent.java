package com.roquito.platform.messaging;

import org.springframework.web.socket.WebSocketSession;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.roquito.platform.messaging.protocol.Payload;

public class PayloadEvent {
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

    public final static EventFactory<PayloadEvent> EVENT_FACTORY = new EventFactory<PayloadEvent>() {
	public PayloadEvent newInstance() {
	    return new PayloadEvent();
	}
    };

    public final static EventTranslatorTwoArg<PayloadEvent, Payload, WebSocketSession> EVENT_TRANSLATOR = new EventTranslatorTwoArg<PayloadEvent, Payload, WebSocketSession>() {
	@Override
	public void translateTo(PayloadEvent event, long sequence, Payload payload, WebSocketSession session) {
	    event.setPayload(payload);
	    event.setSession(session);
	    event.setSequence(sequence);
	}
    };
}

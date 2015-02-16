package com.roquito.platform.messaging;

import com.lmax.disruptor.EventFactory;

public class InputEvent {
    private long sequence;
    private Payload message;

    public long getSequence() {
	return sequence;
    }

    public void setSequence(long sequence) {
	this.sequence = sequence;
    }

    public Payload getMessage() {
	return message;
    }

    public void setMessage(Payload message) {
	this.message = message;
    }
    
    public final static EventFactory<InputEvent> EVENT_FACTORY = new EventFactory<InputEvent>() {
        public InputEvent newInstance() {
            return new InputEvent();
        }
    };
}

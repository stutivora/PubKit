package com.roquito.platform.messaging;

import com.lmax.disruptor.EventFactory;

public class OutputEvent {
    
    public final static EventFactory<OutputEvent> EVENT_FACTORY = new EventFactory<OutputEvent>() {
        public OutputEvent newInstance() {
            return new OutputEvent();
        }
    };
}

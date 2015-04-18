package com.pubkit.platform.notification;

import com.lmax.disruptor.EventFactory;

public class BroadcastEvent {
    
    private BroadcastNotification broadcastNotification;
    
    public BroadcastNotification getBroadcastNotification() {
        return broadcastNotification;
    }

    public void setBroadcastNotification(BroadcastNotification broadcastNotification) {
        this.broadcastNotification = broadcastNotification;
    }

    public final static EventFactory<BroadcastEvent> EVENT_FACTORY = new EventFactory<BroadcastEvent>() {
        public BroadcastEvent newInstance() {
            return new BroadcastEvent();
        }
    };
}

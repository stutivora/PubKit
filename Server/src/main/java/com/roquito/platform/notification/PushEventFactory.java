package com.roquito.platform.notification;

import com.lmax.disruptor.EventFactory;

/**
 * Created by puran on 1/30/15.
 */
public class PushEventFactory implements EventFactory<PushEvent> {

    @Override
    public PushEvent newInstance() {
        return new PushEvent();
    }
}

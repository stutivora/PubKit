package com.roquito.platform.messaging;

import com.lmax.disruptor.EventHandler;

public class OutputEventHandler implements EventHandler<OutputEvent> {

    @Override
    public void onEvent(OutputEvent event, long sequence, boolean endOfBatch) throws Exception {
    }

}

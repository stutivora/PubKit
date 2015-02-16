package com.roquito.platform.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class InputEventHandler implements EventHandler<InputEvent> {
    
    private static Logger logger = LoggerFactory.getLogger(InputEventHandler.class);
    
    private static final Map<String, Connection> connections = new HashMap<>();
    
    private static EventTranslatorOneArg<OutputEvent, Payload> OUTPUT_EVENT_TRANSLATOR = null;
    static {
	OUTPUT_EVENT_TRANSLATOR = new EventTranslatorOneArg<OutputEvent, Payload>() {
		@Override
		public void translateTo(OutputEvent event, long sequence, Payload message) {
		}
        };
    }
    
    private RingBuffer<OutputEvent> outputRingBuffer;
    private Disruptor<OutputEvent> outputEventDisruptor;    
    
    public InputEventHandler() {
	startOutputDisruptor();
    }
    
    @SuppressWarnings("unchecked")
    private void startOutputDisruptor() {
        Executor executor = Executors.newCachedThreadPool();
        int bufferSize = 1024 * 32;
        outputEventDisruptor = new Disruptor<>(OutputEvent.EVENT_FACTORY, bufferSize, executor);
        
        OutputEventHandler outputEventHandler = new OutputEventHandler();
        outputEventDisruptor.handleEventsWith(outputEventHandler);

        outputEventDisruptor.start();
        outputRingBuffer = outputEventDisruptor.getRingBuffer();
    }
    
    @Override
    public void onEvent(InputEvent event, long sequence, boolean endOfBatch) throws Exception {
	logger.debug("Message input event received with sequence:"+sequence);
    }
    
    public void sendMessage(Payload message) {
	outputRingBuffer.publishEvent(OUTPUT_EVENT_TRANSLATOR, message);
    }
}

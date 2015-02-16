package com.roquito.platform.messaging;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class ConnectionHandler extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private static EventTranslatorOneArg<InputEvent, Payload> MESSAGE_EVENT_TRANSLATOR = null;
    static {
	MESSAGE_EVENT_TRANSLATOR = new EventTranslatorOneArg<InputEvent, Payload>() {
		@Override
		public void translateTo(InputEvent event, long sequence, Payload message) {
		    event.setMessage(message);
		    event.setSequence(sequence);
		}
        };
    }
    
    /* Initialize components */
    private final Gson gson = new Gson();
    
    private Disruptor<InputEvent> inputEventDisruptor;    
    private RingBuffer<InputEvent> inputRingBuffer;
    private InputEventHandler eventHandler;
   
    @Autowired
    public ConnectionHandler() {
	//start disruptor 
	startInputDisruptor();
    }
    
    @SuppressWarnings("unchecked")
    private void startInputDisruptor() {
        Executor executor = Executors.newCachedThreadPool();
        int bufferSize = 1024 * 32;
        inputEventDisruptor = new Disruptor<>(InputEvent.EVENT_FACTORY, bufferSize, executor);
        
        eventHandler = new InputEventHandler();
	inputEventDisruptor.handleEventsWith(eventHandler);

	// Start the Disruptor, starts all threads running and get input ring buffer
        inputEventDisruptor.start();
        inputRingBuffer = inputEventDisruptor.getRingBuffer();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
	logger.debug("Opened new session in instance " + this);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	String messagePayload = message.getPayload();
	logger.debug("Received message payload");
	Payload payload = gson.fromJson(messagePayload, Payload.class);
	if (payload != null) {
	    //publish to disruptor queue
	    inputRingBuffer.publishEvent(MESSAGE_EVENT_TRANSLATOR, payload);
	} else {
	    logger.debug("Null message received from client");
	}
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	session.close(CloseStatus.SERVER_ERROR);
    }
}

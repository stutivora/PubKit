package com.roquito.platform.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.roquito.platform.messaging.protocol.Payload;
import com.roquito.platform.service.QueueService;

public class WebSocketConnectionHandler extends TextWebSocketHandler {
    
    private static Logger LOG = LoggerFactory.getLogger(WebSocketConnectionHandler.class);
    
    private QueueService queueService;
    
    /* Initialize components */
    private final Gson gson = new Gson();
    
    @Autowired
    public WebSocketConnectionHandler(QueueService queueService) {
        this.queueService = queueService;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOG.debug("Opened new session in instance " + this);
    }
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messagePayload = message.getPayload();
        LOG.debug("Received message payload");
        Payload payload = gson.fromJson(messagePayload, Payload.class);
        if (payload != null) {
            // publish to disruptor queue
            this.queueService.publishInputMessageEvent(payload, session);
        } else {
            LOG.debug("Null message received from client");
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }
}

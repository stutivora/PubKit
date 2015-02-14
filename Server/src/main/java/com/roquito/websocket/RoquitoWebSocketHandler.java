package com.roquito.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.roquito.platform.service.UserService;

public class RoquitoWebSocketHandler extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(RoquitoWebSocketHandler.class);

    private final UserService userService;

    @Autowired
    public RoquitoWebSocketHandler(UserService userService) {
	this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
	logger.debug("Opened new session in instance " + this);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	String echoMessage = userService.getNextUserId();
	logger.debug(echoMessage);
	session.sendMessage(new TextMessage(echoMessage));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	session.close(CloseStatus.SERVER_ERROR);
    }
}

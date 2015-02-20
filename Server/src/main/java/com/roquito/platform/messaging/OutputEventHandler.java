package com.roquito.platform.messaging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.lmax.disruptor.EventHandler;
import com.roquito.platform.messaging.persistence.MapDB;
import com.roquito.platform.messaging.protocol.ConnAck;
import com.roquito.platform.messaging.protocol.Disconnect;
import com.roquito.platform.messaging.protocol.Payload;
import com.roquito.platform.messaging.protocol.SubsAck;

public class OutputEventHandler implements EventHandler<PayloadEvent> {

    private static Logger logger = LoggerFactory.getLogger(OutputEventHandler.class);
    private Gson gson = new Gson();
    private MapDB dbStore = MapDB.getInstance();

    @Override
    public void onEvent(PayloadEvent event, long sequence, boolean endOfBatch) throws Exception {
	logger.debug("Output event received with sequence:" + sequence);
	Payload payload = event.getPayload();
	WebSocketSession session = event.getSession();

	switch (payload.getType()) {
	case Payload.DISCONNECT:
	    handleDisconnect((Disconnect) payload, session);
	    break;
	case Payload.CONNACK:
	    handleConnAck((ConnAck) payload, session);
	    break;
	case Payload.SUBSACK:
	    handleSubsAck((SubsAck) payload, session);
	}
    }

    private void handleDisconnect(Disconnect disconnect, WebSocketSession session) {
	sendPayload(disconnect, session);
	closeAndInvalidateSession(disconnect.getClientId(), session);
    }

    private void handleConnAck(ConnAck conAck, WebSocketSession session) {
	sendPayload(conAck, session);
    }

    private void handleSubsAck(SubsAck subsAck, WebSocketSession session) {
	sendPayload(subsAck, session);
    }

    private void sendPayload(Payload payload, WebSocketSession session) {
	String payloadData = gson.toJson(payload);
	if (payloadData != null) {
	    TextMessage textMessage = new TextMessage(payloadData);
	    sendTextMessage(textMessage, session);
	}
    }

    private void sendTextMessage(TextMessage textMessage, WebSocketSession session) {
	try {
	    session.sendMessage(textMessage);
	} catch (IOException e) {
	    logger.error("Error sending message to the client", e);
	    closeSession(session);
	}
    }

    private void closeSession(WebSocketSession session) {
	try {
	    session.close(CloseStatus.NORMAL);
	} catch (IOException e) {
	    logger.error("Error closing the connection", e);
	    // Can't do anything now!
	}
    }

    private void closeAndInvalidateSession(String clientId, WebSocketSession session) {
	logger.info("Closing and invalidating client session for {" + clientId + "}");

	dbStore.removeConnection(clientId);
	dbStore.removeSession(session);
	dbStore.invalidateSessionToken(clientId);

	closeSession(session);
    }
}

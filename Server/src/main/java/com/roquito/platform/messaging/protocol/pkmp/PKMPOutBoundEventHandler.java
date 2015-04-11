/* Copyright (c) 2015 32skills Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.roquito.platform.messaging.protocol.pkmp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.lmax.disruptor.EventHandler;
import com.roquito.platform.messaging.protocol.pkmp.proto.PKMPBasePayload;
import com.roquito.platform.messaging.protocol.pkmp.proto.PKMPDisconnect;
import com.roquito.platform.messaging.protocol.pkmp.proto.PKMPPayload;
import com.roquito.platform.service.MessagingService;
/**
 * Disruptor handler for messaging output data so that the data write
 * is not blocking the processing. 
 *  
 * @author puran
 */
public class PKMPOutBoundEventHandler implements EventHandler<PKMPEvent> {
    
    private static Logger LOG = LoggerFactory.getLogger(PKMPOutBoundEventHandler.class);
    private Gson gson = new Gson();
    
    private MessagingService messagingService;
    
    public MessagingService getMessagingService() {
        return messagingService;
    }

    public void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public void onEvent(PKMPEvent event, long sequence, boolean endOfBatch) throws Exception {
        LOG.debug("Output event received with sequence:" + sequence);
        PKMPPayload pKMPPayload = event.getPayload();
        WebSocketSession session = event.getSession();
        
        switch (pKMPPayload.getType()) {
            case PKMPBasePayload.PONG:
                //pong response, send it to the client!
                sendPayload(pKMPPayload, session);
                break;
            case PKMPBasePayload.DISCONNECT:
                handleDisconnect((PKMPDisconnect) pKMPPayload, session);
                break;
            case PKMPBasePayload.CONNACK:
                sendPayload(pKMPPayload, session);
                break;
            case PKMPBasePayload.SUBSACK:
                sendPayload(pKMPPayload, session);
                break;
            case PKMPBasePayload.UNSUBSACK:
                sendPayload(pKMPPayload, session);
                break;
            case PKMPBasePayload.PUBACK:
                sendPayload(pKMPPayload, session);
                break;
            case PKMPBasePayload.MESSAGE:
                sendPayload(pKMPPayload, session);
                break;
            default:
                break;
        }
    }

    private void handleDisconnect(PKMPDisconnect pKMPDisconnect, WebSocketSession session) {
        if (session.isOpen()) {
            sendPayload(pKMPDisconnect, session);
        }
        closeSession(session);
    }
    
    private void sendPayload(PKMPPayload pKMPPayload, WebSocketSession session) {
        String payloadData = gson.toJson(pKMPPayload);
        if (payloadData != null) {
            TextMessage textMessage = new TextMessage(payloadData);
            sendTextMessage(textMessage, session);
        }
    }
    
    private void sendTextMessage(TextMessage textMessage, WebSocketSession session) {
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            LOG.error("Error sending message to the client", e);
            closeSession(session);
        }
    }
    
    private void closeSession(WebSocketSession session) {
        if (!session.isOpen()) {
            return;
        }
        try {
            session.close(CloseStatus.NORMAL);
        } catch (IOException e) {
            LOG.error("Error closing the connection", e);
            // Can't do anything now!
        }
    }
}

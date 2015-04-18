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
package com.pubkit.platform.messaging;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.pubkit.platform.messaging.protocol.pkmp.proto.PKMPBasePayload;
import com.pubkit.platform.service.QueueService;

public class PKMPConnectionHandler extends TextWebSocketHandler implements SubProtocolCapable {
    
    private static Logger LOG = LoggerFactory.getLogger(PKMPConnectionHandler.class);
    private static final String PING = "ping";
    private static final String PONG = "pong";
    
    private final Gson gson = new Gson();
    
    private QueueService queueService;
    
    @Autowired
    public PKMPConnectionHandler(QueueService queueService) {
        this.queueService = queueService;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOG.debug("Opened new session in instance " + this);
    }
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messagePayload = message.getPayload();
        if (PING.equals(messagePayload)) {
            LOG.debug("Received ping, Sending pong response");
            TextMessage textMessage = new TextMessage(PONG);
            session.sendMessage(textMessage);
        } else {
            LOG.debug("Received message payload");
            PKMPBasePayload pKMPBasePayload = gson.fromJson(messagePayload, PKMPBasePayload.class);
            if (pKMPBasePayload != null) {
                // publish to disruptor queue
                this.queueService.publishInputMessageEvent(pKMPBasePayload, session);
            } else {
                LOG.debug("Null message received from client");
            }   
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }
    
    @Override
    public List<String> getSubProtocols() {
        List<String> mqttProtocolVersions = new ArrayList<String>();
        mqttProtocolVersions.add("pkmp");
        
        return mqttProtocolVersions;
    }
}

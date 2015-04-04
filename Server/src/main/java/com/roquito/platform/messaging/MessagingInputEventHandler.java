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
package com.roquito.platform.messaging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import com.lmax.disruptor.EventHandler;
import com.roquito.platform.commons.RoquitoKeyGenerator;
import com.roquito.platform.messaging.protocol.ConnAck;
import com.roquito.platform.messaging.protocol.Connect;
import com.roquito.platform.messaging.protocol.Disconnect;
import com.roquito.platform.messaging.protocol.BasePayload;
import com.roquito.platform.messaging.protocol.Payload;
import com.roquito.platform.messaging.protocol.PubAck;
import com.roquito.platform.messaging.protocol.PubMessage;
import com.roquito.platform.messaging.protocol.Publish;
import com.roquito.platform.messaging.protocol.SubsAck;
import com.roquito.platform.messaging.protocol.Subscribe;
import com.roquito.platform.messaging.protocol.UnSubsAck;
import com.roquito.platform.messaging.protocol.UnSubscribe;
import com.roquito.platform.model.Application;
import com.roquito.platform.service.ApplicationService;
import com.roquito.platform.service.MessagingService;
import com.roquito.platform.service.QueueService;

public class MessagingInputEventHandler implements EventHandler<MessagingEvent> {
    
    private static Logger LOG = LoggerFactory.getLogger(MessagingInputEventHandler.class);
    
    private final RoquitoKeyGenerator keyGenerator = new RoquitoKeyGenerator();

    private ApplicationService applicationService;
    private MessagingService messagingService;
    private QueueService queueService;
    
    public ApplicationService getApplicationService() {
        return applicationService;
    }

    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }

    public void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public QueueService getQueueService() {
        return queueService;
    }

    public void setQueueService(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public void onEvent(MessagingEvent event, long sequence, boolean endOfBatch) throws Exception {
        LOG.debug("Input event received with sequence:" + sequence);
        Payload payload = event.getPayload();
        switch (payload.getType()) {
            case BasePayload.CONNECT:
                handleConnect(new Connect(payload), event.getSession());
                break;
            case BasePayload.SUBSCRIBE:
                handleSubscribe(new Subscribe(payload), event.getSession());
                break;
            case BasePayload.UNSUBSCRIBE:
                handleUnSubscribe(new UnSubscribe(payload), event.getSession());
                break;
            case BasePayload.PUBLISH:
                handlePublish(new Publish(payload), event.getSession());
                break;
            case BasePayload.DISCONNECT:
                sendDisconnect(payload.getClientId(), event.getSession(), "");
                break;
            default:
                break;
        }
    }
    
    private void handleConnect(Connect connect, WebSocketSession session) {
        boolean valid = validateApplication(connect, session);
        if (!valid) {
            sendPayload(new ConnAck(connect.getClientId(), ConnAck.APPLICATION_INVALID), session);
            return;
        }
        LOG.debug("Connecting client {" + connect.getClientId() + "}");
        Connection existingConnection = messagingService.getConnection(connect.getClientId());
        if (existingConnection != null) {
            existingConnection.setSessionId(session.getId());
            messagingService.addConnection(connect.getClientId(), existingConnection);
        } else {
            Connection newConnection = new Connection(connect.getClientId(), session.getId(), connect.getApplicationId(),
                    connect.getApiVersion());
            //add new connection
            messagingService.addConnection(connect.getClientId(), newConnection);
        }
        String accessToken = keyGenerator.getSecureSessionId();
        boolean success = messagingService.saveAccessToken(session.getId(), accessToken);
        if (success) {
            //set active session
            messagingService.addSession(session);
            LOG.debug("Connected client {" + connect.getClientId() + "}");
            sendPayload(new ConnAck(connect.getClientId(), session.getId(), accessToken), session);
        } else {
            LOG.debug("Connection failed for client {" + connect.getClientId() + "}");
            sendPayload(new ConnAck(connect.getClientId(), ConnAck.CONNECTION_FAILED), session);
        }
    }
    
    private void handleSubscribe(Subscribe subscribe, WebSocketSession session) {
        String topic = subscribe.getTopic();
        if (topic == null || StringUtils.isEmpty(topic)) {
            LOG.error("Client {"+ subscribe.getClientId() + "} failed to subscribe topic {"+ subscribe.getTopic() +"}. Invalid topic");
            
            SubsAck errorAck = new SubsAck(subscribe.getClientId(), topic, SubsAck.INVALID_TOPIC);
            sendPayload(errorAck, session);
            
            return;
        }
        boolean tokenValid = validateAccessToken(subscribe.getClientId(), subscribe.getSessionToken(), session);
        if (!tokenValid) {
            LOG.error("Client {"+ subscribe.getClientId() + "} failed to subscribe topic {"+ topic +"}. Invalid token");
            SubsAck errorAck = new SubsAck(subscribe.getClientId(), topic, SubsAck.INVALID_TOKEN);
            sendPayload(errorAck, session);
            
            return;
        }

        Connection connection = messagingService.getConnection(subscribe.getClientId());
        if (connection != null && connection.getSessionId().equals(session.getId())) {
            messagingService.subscribeTopic(topic, connection);
            LOG.debug("Client id {"+ subscribe.getClientId() + "} subscribed to topic {"+ topic +"}");
            
            SubsAck subsAck = new SubsAck(subscribe.getClientId(), topic);
            sendPayload(subsAck, session);
        } else {
            LOG.error("Client {"+ subscribe.getClientId() + "} failed to subscribe topic {"+ topic +"}. No connection found so closing connection");
            
            SubsAck errorAck = new SubsAck(subscribe.getClientId(), topic, SubsAck.CONNECTION_ERROR);
            sendPayload(errorAck, session);
        }
    }
    
    private void handleUnSubscribe(UnSubscribe unsubscribe, WebSocketSession session) {
        String topic = unsubscribe.getTopic();
        if (topic == null || StringUtils.isEmpty(topic)) {
            LOG.error("Client {"+ unsubscribe.getClientId() + "} failed to unsubscribe topic {"+ unsubscribe.getTopic() +"}. Invalid topic");
            
            UnSubsAck errorAck = new UnSubsAck(unsubscribe.getClientId(), topic, SubsAck.INVALID_TOPIC);
            sendPayload(errorAck, session);
            
            return;
        }
        boolean tokenValid = validateAccessToken(unsubscribe.getClientId(), unsubscribe.getSessionToken(), session);
        if (!tokenValid) {
            LOG.error("Client {"+ unsubscribe.getClientId() + "} failed to unsubscribe topic {"+ topic +"}. Invalid token");
            UnSubsAck errorAck = new UnSubsAck(unsubscribe.getClientId(), topic, SubsAck.INVALID_TOKEN);
            sendPayload(errorAck, session);
            
            return;
        }

        Connection connection = messagingService.getConnection(unsubscribe.getClientId());
        if (connection != null && connection.getSessionId().equals(session.getId())) {
            messagingService.subscribeTopic(topic, connection);
            LOG.debug("Client id {"+ unsubscribe.getClientId() + "} unsubscribed to topic {"+ topic +"}");
            
            UnSubsAck unSubsAck = new UnSubsAck(unsubscribe.getClientId(), topic);
            sendPayload(unSubsAck, session);
        } else {
            LOG.error("Client {"+ unsubscribe.getClientId() + "} failed to unsubscribe topic {"+ topic +"}. No connection found so closing connection");
            
            UnSubsAck errorAck = new UnSubsAck(unsubscribe.getClientId(), topic, SubsAck.CONNECTION_ERROR);
            sendPayload(errorAck, session);
        }
    }
    
    private void handlePublish(Publish publish, WebSocketSession session) {
        String topic = publish.getTopic();
        if (topic == null || "".equals(topic)) {
            LOG.debug("Publish failed. Null or empty topic name.");
            
            PubAck errorAck = new PubAck(publish.getClientId(), topic, PubAck.INVALID_TOPIC);
            sendPayload(errorAck, session);
            
            return;
        }
        List<Connection> subscribers = messagingService.getAllSubscribers(topic);
        for (Connection subscriber : subscribers) {
            if (subscriber.getClientId().equals(publish.getClientId())) {
                continue;
            }
            WebSocketSession subscriberSession = messagingService.getSession(subscriber.getSessionId());
            if (subscriberSession != null && subscriberSession.isOpen()) {
                PubMessage pubMessage = new PubMessage(subscriber.getClientId(), publish.getClientId());
                
                pubMessage.setData(publish.getData());
                pubMessage.addHeader(BasePayload.APP_ID, publish.getApplicationId());
                pubMessage.setTopic(publish.getTopic());
                
                sendPayload(pubMessage, subscriberSession);
            } else {
                LOG.debug("Session not found for client with client id {" + subscriber.getClientId() + "} "
                        + "and session id {" + subscriber.getSessionId() + "}");
                
                // send push notification if possible for inactive subscriber
                handleInactiveSubscriber(subscriber);
                
                if (subscriberSession != null && !subscriberSession.isOpen()) {
                    sendDisconnect(subscriber.getClientId(), subscriberSession, "");
                }
            }
        }
        sendPayload(new PubAck(publish.getClientId(), topic), session);
    }
    
    private void sendDisconnect(String clientId, WebSocketSession session, String data) {
        LOG.info("Closing and invalidating client session for {" + clientId + "}");
        
        messagingService.removeConnection(clientId);
        messagingService.removeSession(session);
        messagingService.invalidateSessionToken(clientId);
        
        Disconnect disconnect = new Disconnect(clientId);
        disconnect.setData(data);
        sendPayload(disconnect, session);
    }
    
    private boolean validateAccessToken(String clientId, String accessToken, WebSocketSession session) {
        boolean tokenValid = messagingService.isAccessTokenValid(accessToken);
        if (!tokenValid) {
            Disconnect disconnect = new Disconnect(clientId);
            LOG.debug("Invalid access token. Closing the client connection {" + clientId + "}");
            disconnect.setData("Invalid access token. Closing the client connection {" + clientId + "}");
            sendPayload(disconnect, session);
            
            return false;
        }
        return true;
    }
    
    private boolean validateApplication(Connect connect, WebSocketSession session) {
        String applicationId = connect.getApplicationId();
        
        String closeMessage = null;
        boolean valid = true;
        if (applicationId == null) {
            LOG.debug("Null application id received, closing connection");
            closeMessage = "Null application id received, closing connection";
            valid = false;
        }
        Application savedApplication = applicationService.findByApplicationId(applicationId);
        if (savedApplication == null) {
            LOG.debug("Application not found, closing connection");
            closeMessage = "Application not found, closing connection";
            valid = false;
        }
        if (!savedApplication.getApplicationKey().equals(connect.getApiKey())) {
            LOG.debug("Application key does not match, closing connection");
            closeMessage = "Application key does not match, closing connection";
            valid = false;
        }
        if (!valid) {
            Disconnect disconnect = new Disconnect(connect.getClientId());
            disconnect.setData(closeMessage);
            sendPayload(disconnect, session);
        }
        return valid;
    }
    
    private void handleInactiveSubscriber(Connection subscriber) {
        
    }
    
    public void sendPayload(BasePayload basePayload, WebSocketSession session) {
        this.queueService.publishOutputMessageEvent(basePayload, session);
    }
}

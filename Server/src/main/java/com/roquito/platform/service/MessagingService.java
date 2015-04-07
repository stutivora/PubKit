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
package com.roquito.platform.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.roquito.RoquitoConfig;
import com.roquito.platform.messaging.Connection;
import com.roquito.web.exception.RoquitoServerException;

@Service
public class MessagingService {
    
    private static final Logger LOG = LoggerFactory.getLogger(MessagingService.class);
    
    /* MAP DB reference */
    private DB internalDB;
    
    /* Maps the clientId to connection object */
    private HTreeMap<String, Connection> connectionStore = null;
    
    /* Subscription store */
    private HTreeMap<String, Set<Connection>> subscriptionStore = null;
    
    /* Access token store */
    private HTreeMap<String, String> tokenStore = null;
    
    /* Session map that olds current active session */
    private Map<String, WebSocketSession> sessionMap = new HashMap<>();
    
    @Autowired
    public MessagingService(RoquitoConfig config) {
        initMapDB(config.getMapdbFilePath(), config.getMapdbPassword());
    }
    
    public void initMapDB(String mapdbFilePath, String encryptedMapdbPassword) {
        if (mapdbFilePath == null || encryptedMapdbPassword == null) {
            throw new RoquitoServerException("Missing mapdb configuration.");
        }
        // configure and open database using builder pattern.
        // all options are available with code auto-completion.
        internalDB = DBMaker.newFileDB(new File(mapdbFilePath))
                            .closeOnJvmShutdown()
                            .mmapFileEnableIfSupported()
                            .encryptionEnable(encryptedMapdbPassword)
                            .checksumEnable()
                            .make();
        
        connectionStore = internalDB.getHashMap("connectionStore");
        subscriptionStore = internalDB.getHashMap("subscriptionStore");
        tokenStore = internalDB.createHashMap("tokenStore").expireAfterWrite(2, TimeUnit.HOURS).makeOrGet();
    }
    
    public boolean saveAccessToken(String clientId, String accessToken) {
        boolean success = false;
        if (!tokenStore.containsKey(accessToken)) {
            tokenStore.put(accessToken, clientId);
            tokenStore.put(clientId, accessToken);
            
            internalDB.commit();
            success = true;
        }
        return success;
    }
    
    public void invalidateSessionToken(String clientId) {
        if (tokenStore.containsKey(clientId)) {
            String accessToken = tokenStore.get(clientId);
            tokenStore.remove(accessToken);
            tokenStore.remove(clientId);
            
            internalDB.commit();
        }
    }
    
    public boolean isAccessTokenValid(String accessToken) {
        return tokenStore.containsKey(accessToken);
    }
    
    public WebSocketSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
    
    public void addSession(WebSocketSession session) {
        if (session == null) {
            return;
        }
        if (!sessionMap.containsKey(session.getId())) {
            sessionMap.put(session.getId(), session);
        }
    }
    
    public void removeSession(WebSocketSession session) {
        if (session == null) {
            return;
        }
        if (sessionMap.containsKey(session.getId())) {
            sessionMap.remove(session.getId());
        }
    }
    
    public void addConnection(String clientId, Connection connection) {
        if (connectionStore.containsKey(clientId)) {
            connectionStore.remove(clientId);
        }
        connectionStore.put(clientId, connection);
        internalDB.commit();
    }
    
    public boolean removeConnection(String clientId) {
        if (connectionStore.containsKey(clientId)) {
            connectionStore.remove(clientId);
            
            internalDB.commit();
            return true;
        }
        return false;
    }
    
    public Connection getConnection(String clientId) {
        return connectionStore.get(clientId);
    }
    
    public void subscribeTopic(String topic, Connection connection) {
        Set<Connection> subscriptions = subscriptionStore.get(topic);
        if (subscriptions == null) {
            subscriptions = new HashSet<Connection>();
            subscriptionStore.put(topic, subscriptions);
        }
        if (!subscriptions.contains(connection)) {
            subscriptions.add(connection);
        }
        LOG.info("Number of subscribers for topic: {"+ topic + "} is:"+ subscriptions.size());
        internalDB.commit();
    }
    
    public void unsubscribeTopic(String topic, Connection connection) {
        Set<Connection> subscriptions = subscriptionStore.get(topic);
        if (subscriptions != null) {
            for (Connection subscription : subscriptions) {
                if (connection.getClientId().equals(subscription.getClientId())) {
                    subscriptions.remove(subscription);
                }
            }
        }
        LOG.info("Number of subscribers for topic: {"+ topic + "} is:"+ subscriptions.size());
        internalDB.commit();
    }
    
    public List<Connection> getAllSubscribers(String topic) {
        List<Connection> results = new ArrayList<>();
        
        Set<Connection> subscriptions = subscriptionStore.get(topic);
        if (subscriptions != null && subscriptions.size() > 0) {
            results.addAll(subscriptions);
        }
        return Collections.unmodifiableList(results);
    }
    
    @PreDestroy
    public void closeDB() {
        LOG.info("Closing map db database after shutdown");
        internalDB.close();
    }
}

package com.pubkit.platform.service.impl;

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

import com.pubkit.PubKitConfig;
import com.pubkit.platform.messaging.protocol.pkmp.PKMPConnection;
import com.pubkit.platform.service.MessageStoreService;
import com.pubkit.web.exception.RoquitoServerException;
/**
 * Created by puran
 */
@Service
public class MessageStoreServiceImpl implements MessageStoreService {
    
    private static final Logger LOG = LoggerFactory.getLogger(MessageStoreService.class);
    
    /* MAP DB reference */
    private DB internalDB;
    
    /* Maps the clientId to connection object */
    private HTreeMap<String, PKMPConnection> connectionStore = null;
    
    /* Subscription store */
    private HTreeMap<String, Set<PKMPConnection>> subscriptionStore = null;
    
    /* Access token store */
    private HTreeMap<String, String> tokenStore = null;
    
    /* Session map that olds current active session */
    private Map<String, WebSocketSession> sessionMap = new HashMap<>();
    
    @Autowired
    public MessageStoreServiceImpl(PubKitConfig config) {
        initMapDB(config.getMapdbFilePath(), config.getMapdbPassword());
    }
    
    private void initMapDB(String mapdbFilePath, String encryptedMapdbPassword) {
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
    
    public void addConnection(String clientId, PKMPConnection pKMPConnection) {
        if (connectionStore.containsKey(clientId)) {
            connectionStore.remove(clientId);
        }
        connectionStore.put(clientId, pKMPConnection);
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
    
    public PKMPConnection getConnection(String clientId) {
        return connectionStore.get(clientId);
    }
    
    public void subscribeTopic(String topic, PKMPConnection pKMPConnection) {
        Set<PKMPConnection> subscriptions = subscriptionStore.get(topic);
        if (subscriptions == null) {
            subscriptions = new HashSet<PKMPConnection>();
            subscriptionStore.put(topic, subscriptions);
        }
        if (!subscriptions.contains(pKMPConnection)) {
            subscriptions.add(pKMPConnection);
        }
        LOG.info("Number of subscribers for topic: {"+ topic + "} is:"+ subscriptions.size());
        internalDB.commit();
    }
    
    public void unsubscribeTopic(String topic, PKMPConnection pKMPConnection) {
        Set<PKMPConnection> subscriptions = subscriptionStore.get(topic);
        if (subscriptions != null) {
            for (PKMPConnection subscription : subscriptions) {
                if (pKMPConnection.getClientId().equals(subscription.getClientId())) {
                    subscriptions.remove(subscription);
                }
            }
        }
        LOG.info("Number of subscribers for topic: {"+ topic + "} is:"+ subscriptions.size());
        internalDB.commit();
    }
    
    public List<PKMPConnection> getAllSubscribers(String topic) {
        List<PKMPConnection> results = new ArrayList<>();
        
        Set<PKMPConnection> subscriptions = subscriptionStore.get(topic);
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

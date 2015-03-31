package com.roquito.platform.messaging.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import com.roquito.platform.messaging.Connection;
import com.roquito.web.exception.RoquitoServerException;

@Repository
public final class MapDB {
    /* Singleton Instance */
    //private final static MapDB INSTANCE = new MapDB();

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
    
    public static MapDB getInstance() {
	return null;
    }
    
    @Autowired
    public MapDB(@Value("${mapdb.filepath}") String filePath, @Value("${mapdb.encryptedPassword}") String password) {
	initMapDB(filePath, password);
    }

    public void initMapDB(String mapdbFilePath, String encryptedMapdbPassword) {
	if (mapdbFilePath == null || encryptedMapdbPassword == null) {
	    throw new RoquitoServerException("Missing mapdb configuration.");
	}
	// configure and open database using builder pattern.
	// all options are available with code auto-completion.
	internalDB = DBMaker.newFileDB(new File(mapdbFilePath)).closeOnJvmShutdown().transactionDisable()
		.mmapFileEnableIfSupported().encryptionEnable(encryptedMapdbPassword).make();

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
	if (!sessionMap.containsKey(session.getId())) {
	    sessionMap.put(session.getId(), session);
	}
    }
    
    public void removeSession(WebSocketSession session) {
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
	subscriptions.add(connection);
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
}
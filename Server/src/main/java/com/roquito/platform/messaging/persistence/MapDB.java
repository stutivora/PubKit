package com.roquito.platform.messaging.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.mapdb.*;
import org.springframework.beans.factory.annotation.Value;

import com.roquito.platform.messaging.Connection;
import com.roquito.web.exception.RoquitoServerException;

public final class MapDB {
    /* Singleton Instance */
    private final static MapDB INSTANCE = new MapDB();

    @Value("${mapdb.filepath}")
    private static String mapdbFilePath;

    @Value("${mapdb.encryptedPassword}")
    private static String encryptedMapdbPassword;

    /* MAP DB reference */
    private DB internalDB;

    /* Maps the clientId to connection object */
    private HTreeMap<String, Connection> connectionStore = null;

    /* Subscription store */
    private HTreeMap<String, Set<Connection>> subscriptionStore = null;

    /* Access token store */
    private HTreeMap<String, String> tokenStore = null;

    public static MapDB getInstance() {
	return INSTANCE;
    }

    public void initMapDB() {
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

    public boolean saveAccessToken(String email, String accessToken) {
	boolean success = false;
	if (!tokenStore.containsKey(accessToken)) {
	    tokenStore.put(accessToken, email);
	    internalDB.commit();
	    success = true;
	}
	return success;
    }

    public boolean isAccessTokenValid(String accessToken) {
	return tokenStore.containsKey(accessToken);
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

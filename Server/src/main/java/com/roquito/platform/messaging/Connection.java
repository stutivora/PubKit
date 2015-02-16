package com.roquito.platform.messaging;

public final class Connection {
    private String clientId;
    private String sessionId;
    private String apiKey;
    private String apiVersion;

    public Connection(String clientId, String sessionId, String apiKey, String apiVersion) {
	super();
	this.clientId = clientId;
	this.sessionId = sessionId;
	this.apiKey = apiKey;
	this.apiVersion = apiVersion;
    }

    public String getClientId() {
	return clientId;
    }

    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    public String getSessionId() {
	return sessionId;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
    }

    public String getApiKey() {
	return apiKey;
    }

    public void setApiKey(String apiKey) {
	this.apiKey = apiKey;
    }

    public String getApiVersion() {
	return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
	this.apiVersion = apiVersion;
    }
}

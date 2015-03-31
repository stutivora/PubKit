package com.roquito.platform.messaging;

public final class Connection {
    private String clientId;
    private String sessionId;
    private String appId;
    private String apiVersion;

    public Connection(String clientId, String sessionId, String appId, String apiVersion) {
	super();
	this.clientId = clientId;
	this.sessionId = sessionId;
	this.appId = appId;
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

    public String getAppId() {
	return appId;
    }

    public void setAppId(String appId) {
	this.appId = appId;
    }

    public String getApiVersion() {
	return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
	this.apiVersion = apiVersion;
    }
}

package com.roquito.platform.messaging.protocol;

public class Connect extends Payload {

    private static final String API_KEY = "API_KEY";
    private static final String API_VERSION = "API_VERSION";

    public String getApiKey() {
	return getHeaders().get(API_KEY);
    }

    public String getApiVersion() {
	return getHeaders().get(API_VERSION);
    }
}

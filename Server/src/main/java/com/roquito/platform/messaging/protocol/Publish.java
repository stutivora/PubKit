package com.roquito.platform.messaging.protocol;

public class Publish extends Payload {

    public String getTopic() {
	return getHeader(TOPIC);
    }

    public boolean isRetry() {
	String retryValue = getHeader(RETRY);
	return Boolean.getBoolean(retryValue);
    }

    public boolean isPersist() {
	String persistValue = getHeader(PERSIST);
	return Boolean.getBoolean(persistValue);
    }
}

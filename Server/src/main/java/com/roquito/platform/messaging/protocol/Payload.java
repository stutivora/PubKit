package com.roquito.platform.messaging.protocol;

import java.util.HashMap;
import java.util.Map;

public class Payload {

    public static final String CONNECT = "CONNECT";
    public static final String CONNACK = "CONNACK";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String SUBSCRIBE = "SUBSCRIBE";
    public static final String SUBSACK = "SUBSACK";
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";
    public static final String UNSUBSACK = "UNSUBSACK";
    public static final String PUBLISH = "PUBLISH";
    public static final String PUBACK = "PUBACK";

    public static final String APP_ID = "application_id";
    public static final String CLIENT_ID = "client_id";
    public static final String SESSION_ID = "session_id";
    public static final String SESSION_TOKEN = "session_token";
    public static final String TYPE = "type";
    

    private Map<String, String> headers;
    private String data;

    public Map<String, String> getHeaders() {
	return headers;
    }

    public void setHeaders(Map<String, String> headers) {
	this.headers = headers;
    }
    
    public void addHeader(String header, String value) {
	if (headers == null) {
	    headers = new HashMap<>();
	}
	headers.put(header, value);
    }
    
    public String getApplicationId() {
	return headers.get(APP_ID);
    }

    public String getData() {
	return data;
    }

    public void setData(String data) {
	this.data = data;
    }

    public String getType() {
	return headers.get(TYPE);
    }

    public String getClientId() {
	return headers.get(CLIENT_ID);
    }
}

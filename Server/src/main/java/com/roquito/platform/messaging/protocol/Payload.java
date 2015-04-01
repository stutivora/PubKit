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
    public static final String MESSAGE = "MESSAGE";
    
    public static final String APP_ID = "application_id";
    public static final String CLIENT_ID = "client_id";
    public static final String SENDER_ID = "sender_id";
    public static final String SESSION_ID = "session_id";
    public static final String SESSION_TOKEN = "session_token";
    public static final String TYPE = "type";
    public static final String TOPIC = "topic";
    public static final String RETRY = "retry";
    public static final String PERSIST = "persist";
    
    private Map<String, String> headers;
    private String data;
    
    public Payload() {
    }
    
    public Payload(String clientId) {
        addHeader(CLIENT_ID, clientId);
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public void addHeader(String header, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(header, value);
    }
    
    public String getHeader(String header) {
        if (headers != null) {
            return headers.get(header);
        }
        return null;
    }
    
    public String getType() {
        return getHeader(TYPE);
    }
    
    public String getApplicationId() {
        return getHeader(APP_ID);
    }
    
    public String getSessionToken() {
        return getHeader(SESSION_TOKEN);
    }
    
    public String getClientId() {
        return getHeader(CLIENT_ID);
    }
}

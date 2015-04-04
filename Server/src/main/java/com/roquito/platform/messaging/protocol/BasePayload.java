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
package com.roquito.platform.messaging.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the base payload class that represents the PubKit's websocket 
 * data format.
 * 
 * Sample CONNECT payload 
 * <p>
 *  {
 *      "headers":{
 *                  "type":"PUBLISH", 
 *                  "client_id":"el8mxkhGik", 
 *                  "application_id":"1339208702",
 *                  "session_token":"39k30202nwskdkd0222"
 *                }, 
 *      "data":""
 *  }
 * </p>
 * 
 * <tt>headers</tt> contains protocol data and other required information in key/value pair.
 * 
 * <tt>data</tt> can be plain text or string value of JSON data. Client applications can use 
 * this value to trasnfer application data.
 * 
 * @author puran
 */
public class BasePayload implements Payload {
    
    public static final String PONG = "PONG";
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
    public static final String TIMESTAMP = "timestamp";
    public static final String TOPIC = "topic";
    public static final String STATUS = "status";
    public static final String RETRY = "retry";
    public static final String PERSIST = "persist";
    
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String CONNECTION_ERROR = "CONNECTION_ERROR";
    public static final String INVALID_TOPIC = "INVALID_TOPIC";
    
    private Map<String, String> headers;
    private String data;
    
    public BasePayload() {
        
    }
    
    public BasePayload(Payload payload) {
        BasePayload basePayload = (BasePayload)payload;
        
        this.setHeaders(basePayload.getHeaders());
        this.setData(basePayload.getData());
    }
    
    public BasePayload(String clientId) {
        if (clientId != null) {
            addHeader(CLIENT_ID, clientId);   
        }
        addHeader(STATUS, "OK");
        addHeader(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    }
    
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
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
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

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
package com.pubkit.platform.service;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.pubkit.platform.messaging.protocol.pkmp.PKMPConnection;

public interface MessageStoreService {
    
    boolean saveAccessToken(String clientId, String accessToken);
    
    void invalidateSessionToken(String clientId);
    
    boolean isAccessTokenValid(String accessToken);
    
    WebSocketSession getSession(String sessionId);
    
    void addSession(WebSocketSession session);
    
    void removeSession(WebSocketSession session);
    
    void addConnection(String clientId, PKMPConnection pKMPConnection);
    
    boolean removeConnection(String clientId);
    
    PKMPConnection getConnection(String clientId);
    
    void subscribeTopic(String topic, PKMPConnection pKMPConnection);
    
    void unsubscribeTopic(String topic, PKMPConnection pKMPConnection);
    
    List<PKMPConnection> getAllSubscribers(String topic);
    
    void closeDB();
}

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

import org.springframework.web.socket.WebSocketSession;

import com.pubkit.platform.messaging.protocol.pkmp.proto.PKMPBasePayload;
import com.pubkit.platform.messaging.protocol.pkmp.proto.PKMPPayload;
import com.pubkit.platform.notification.BroadcastNotification;
import com.pubkit.platform.notification.SimpleApnsPushNotification;
import com.pubkit.platform.notification.SimpleGcmPushNotification;

/**
 * Created by puran
 */
public interface QueueService {
    
    void sendGcmPushNotification(SimpleGcmPushNotification gcmNotification);
    
    void sendApnsPushNotification(SimpleApnsPushNotification apnsNotification);
    
    void sendBroadcastPushNotification(BroadcastNotification broadcastNotification);
    
    void publishInputMessageEvent(PKMPBasePayload pKMPBasePayload, WebSocketSession session);
    
    void publishOutputMessageEvent(PKMPPayload pKMPPayload, WebSocketSession session);
}

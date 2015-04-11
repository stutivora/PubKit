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
package com.roquito.platform.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.roquito.platform.messaging.protocol.pkmp.proto.PKMPBasePayload;
import com.roquito.platform.messaging.protocol.pkmp.proto.PKMPPayload;
import com.roquito.platform.messaging.protocol.pkmp.PKMPEvent;
import com.roquito.platform.messaging.protocol.pkmp.PKMPInBoundEventHandler;
import com.roquito.platform.messaging.protocol.pkmp.PKMPOutBoundEventHandler;
import com.roquito.platform.notification.SimpleApnsPushNotification;
import com.roquito.platform.notification.BroadcastEvent;
import com.roquito.platform.notification.BroadcastEventHandler;
import com.roquito.platform.notification.BroadcastNotification;
import com.roquito.platform.notification.SimpleGcmPushNotification;
import com.roquito.platform.notification.PushEventFactory;
import com.roquito.platform.notification.PushEventHandler;
import com.roquito.platform.notification.PushNotification;
import com.roquito.platform.notification.PushType;
import com.roquito.platform.notification.PushEvent;

/**
 * Created by puran
 */
@Service
public class QueueService {
    
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);
    
    /* Disruptors */
    private Disruptor<BroadcastEvent> broadcastEventDisruptor;
    private Disruptor<PushEvent> pushEventDisruptor;
    private Disruptor<PKMPEvent> messageInputEventDisruptor;
    private Disruptor<PKMPEvent> messageOutputEventDisruptor;
    
    /* Ring Buffers */
    private RingBuffer<BroadcastEvent> broadcastRingBuffer;
    private RingBuffer<PushEvent> pushRingBuffer;
    private RingBuffer<PKMPEvent> messageInputRingBuffer;
    private RingBuffer<PKMPEvent> messageOutputRingBuffer;
    
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private StatsService statsService;
    
    private static final EventTranslatorOneArg<BroadcastEvent, BroadcastNotification> BROADCAST_EVENT_TRANSLATOR = new EventTranslatorOneArg<BroadcastEvent, BroadcastNotification>() {
        @Override
        public void translateTo(BroadcastEvent event, long sequence, BroadcastNotification broadcastNotification) {
            event.setBroadcastNotification(broadcastNotification);
        }
    };
    
    private static final EventTranslatorTwoArg<PushEvent, PushNotification, PushType> PUSH_EVENT_TRANSLATOR = new EventTranslatorTwoArg<PushEvent, PushNotification, PushType>() {
        @Override
        public void translateTo(PushEvent event, long sequence, PushNotification pushNotification, PushType pushType) {
            event.setPushNotification(pushNotification);
            event.setPushType(pushType);
        }
    };
    
    private static EventTranslatorTwoArg<PKMPEvent, PKMPPayload, WebSocketSession> MESSAGE_INPUT_EVENT_TRANSLATOR = null;
    static {
        MESSAGE_INPUT_EVENT_TRANSLATOR = new EventTranslatorTwoArg<PKMPEvent, PKMPPayload, WebSocketSession>() {
            @Override
            public void translateTo(PKMPEvent event, long sequence, PKMPPayload pKMPPayload, WebSocketSession session) {
                event.setPayload(pKMPPayload);
                event.setSequence(sequence);
                event.setSession(session);
            }
        };
    }
    
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void start() {
        LOG.info("Initializing push service");
        
        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();
        
        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;
        
        // The factory for the event
        PushEventFactory pushEventFactory = new PushEventFactory();
        
        // Construct the Disruptors
        this.pushEventDisruptor = new Disruptor<>(pushEventFactory, bufferSize, executor);
        this.broadcastEventDisruptor = new Disruptor<>(BroadcastEvent.EVENT_FACTORY, bufferSize, executor);
        this.messageInputEventDisruptor = new Disruptor<>(PKMPEvent.EVENT_FACTORY, bufferSize, executor);
        this.messageOutputEventDisruptor = new Disruptor<>(PKMPEvent.EVENT_FACTORY, bufferSize, executor);
        
        // PKMPConnect the handlers
        PushEventHandler pushEventHandler = new PushEventHandler(applicationService, deviceInfoService);
        EventHandlerGroup<PushEvent> handlerGroup = this.pushEventDisruptor.handleEventsWith(pushEventHandler);
        if (handlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for push event handler");
        }
        
        BroadcastEventHandler broadcastHandler = new BroadcastEventHandler(deviceInfoService, this);
        EventHandlerGroup<BroadcastEvent> bcHandlerGroup = this.broadcastEventDisruptor.handleEventsWith(broadcastHandler);
        if (bcHandlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for broadcast event handler");
        }
        
        PKMPInBoundEventHandler inputEventHandler = new PKMPInBoundEventHandler();
        inputEventHandler.setApplicationService(applicationService);
        inputEventHandler.setMessagingService(messagingService);
        inputEventHandler.setDeviceInfoService(deviceInfoService);
        inputEventHandler.setStatsService(statsService);
        inputEventHandler.setQueueService(this);
        
        EventHandlerGroup<PKMPEvent> mIhandlerGroup = this.messageInputEventDisruptor.handleEventsWith(inputEventHandler);
        if (mIhandlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for messaging input event handler");
        }
        
        PKMPOutBoundEventHandler outputEventHandler = new PKMPOutBoundEventHandler();
        outputEventHandler.setMessagingService(messagingService);
        
        EventHandlerGroup<PKMPEvent> mOhandlerGroup = this.messageOutputEventDisruptor.handleEventsWith(outputEventHandler);
        if (mOhandlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for messaging output event handler");
        }
        
        // Start the Disruptor, starts all threads running and get input ring buffer
        startDisruptors();
        LOG.info("Queue service initialized");
    }
    
    public void sendGcmPushNotification(SimpleGcmPushNotification gcmNotification) {
        this.pushRingBuffer.publishEvent(PUSH_EVENT_TRANSLATOR, gcmNotification, PushType.GCM);
    }
    
    public void sendApnsPushNotification(SimpleApnsPushNotification apnsNotification) {
        this.pushRingBuffer.publishEvent(PUSH_EVENT_TRANSLATOR, apnsNotification, PushType.APNS);
    }
    
    public void sendBroadcastPushNotification(BroadcastNotification broadcastNotification) {
        this.broadcastRingBuffer.publishEvent(BROADCAST_EVENT_TRANSLATOR, broadcastNotification);
    }
    
    public void publishInputMessageEvent(PKMPBasePayload pKMPBasePayload, WebSocketSession session) {
        this.messageInputRingBuffer.publishEvent(MESSAGE_INPUT_EVENT_TRANSLATOR, pKMPBasePayload, session);
    }
    
    public void publishOutputMessageEvent(PKMPPayload pKMPPayload, WebSocketSession session) {
        this.messageOutputRingBuffer.publishEvent(MESSAGE_INPUT_EVENT_TRANSLATOR, pKMPPayload, session);
    }
    
    private void startDisruptors() {
        LOG.info("Starting disruptors...");
        
        this.pushEventDisruptor.start();
        this.broadcastEventDisruptor.start();
        this.messageInputEventDisruptor.start();
        this.messageOutputEventDisruptor.start();
        
        this.pushRingBuffer = this.pushEventDisruptor.getRingBuffer();
        this.broadcastRingBuffer = this.broadcastEventDisruptor.getRingBuffer();
        this.messageInputRingBuffer = this.messageInputEventDisruptor.getRingBuffer();
        this.messageOutputRingBuffer = this.messageOutputEventDisruptor.getRingBuffer();
        
        LOG.info("Disruptors started");
    }
}

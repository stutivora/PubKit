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

import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.roquito.platform.messaging.MessagingEvent;
import com.roquito.platform.messaging.MessagingInputEventHandler;
import com.roquito.platform.messaging.MessagingOutputEventHandler;
import com.roquito.platform.messaging.protocol.Payload;
import com.roquito.platform.notification.ApnsPushNotification;
import com.roquito.platform.notification.GcmPushNotification;
import com.roquito.platform.notification.PushEvent;
import com.roquito.platform.notification.PushEventFactory;
import com.roquito.platform.notification.PushEventHandler;
import com.roquito.platform.notification.PushNotification;
import com.roquito.platform.notification.PushType;

/**
 * Created by puran
 */
@Service
public class QueueService {
    
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);
    
    /* Disruptors */
    private Disruptor<PushEvent> pushEventDisruptor;
    private Disruptor<MessagingEvent> messageInputEventDisruptor;
    private Disruptor<MessagingEvent> messageOutputEventDisruptor;
    
    /* Ring Buffers */
    private RingBuffer<PushEvent> pushRingBuffer;
    private RingBuffer<MessagingEvent> messageInputRingBuffer;
    private RingBuffer<MessagingEvent> messageOutputRingBuffer;
    
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessagingService messagingService;
    
    private static final EventTranslatorTwoArg<PushEvent, PushNotification, PushType> PUSH_EVENT_TRANSLATOR = new EventTranslatorTwoArg<PushEvent, PushNotification, PushType>() {
        @Override
        public void translateTo(PushEvent event, long sequence, PushNotification pushNotification, PushType pushType) {
            event.setPushNotification(pushNotification);
            event.setPushType(pushType);
        }
    };
    
    private static EventTranslatorTwoArg<MessagingEvent, Payload, WebSocketSession> MESSAGE_INPUT_EVENT_TRANSLATOR = null;
    static {
        MESSAGE_INPUT_EVENT_TRANSLATOR = new EventTranslatorTwoArg<MessagingEvent, Payload, WebSocketSession>() {
            @Override
            public void translateTo(MessagingEvent event, long sequence, Payload payload, WebSocketSession session) {
                event.setPayload(payload);
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
        this.messageInputEventDisruptor = new Disruptor<>(MessagingEvent.EVENT_FACTORY, bufferSize, executor);
        this.messageOutputEventDisruptor = new Disruptor<>(MessagingEvent.EVENT_FACTORY, bufferSize, executor);
        
        // Connect the handlers
        PushEventHandler pushEventHandler = new PushEventHandler(applicationService, deviceInfoService);
        EventHandlerGroup<PushEvent> handlerGroup = this.pushEventDisruptor.handleEventsWith(pushEventHandler);
        if (handlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for push event handler");
        }
        
        MessagingInputEventHandler inputEventHandler = new MessagingInputEventHandler();
        inputEventHandler.setApplicationService(applicationService);
        inputEventHandler.setMessagingService(messagingService);
        inputEventHandler.setQueueService(this);
        
        EventHandlerGroup<MessagingEvent> mIhandlerGroup = this.messageInputEventDisruptor.handleEventsWith(inputEventHandler);
        if (mIhandlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for messaging input event handler");
        }
        
        MessagingOutputEventHandler outputEventHandler = new MessagingOutputEventHandler();
        outputEventHandler.setMessagingService(messagingService);
        
        EventHandlerGroup<MessagingEvent> mOhandlerGroup = this.messageOutputEventDisruptor.handleEventsWith(outputEventHandler);
        if (mOhandlerGroup == null) {
            LOG.debug("Error creating disruptor handler group for messaging output event handler");
        }
        
        // Start the Disruptor, starts all threads running and get input ring buffer
        startDisruptors();
        LOG.info("Queue service initialized");
    }
    
    public void sendGcmPushNotification(GcmPushNotification gcmNotification) {
        this.pushRingBuffer.publishEvent(PUSH_EVENT_TRANSLATOR, gcmNotification, PushType.GCM);
    }
    
    public void sendApnsPushNotification(ApnsPushNotification apnsNotification) {
        this.pushRingBuffer.publishEvent(PUSH_EVENT_TRANSLATOR, apnsNotification, PushType.APNS);
    }
    
    public void publishInputMessageEvent(Payload payload, WebSocketSession session) {
        this.messageInputRingBuffer.publishEvent(MESSAGE_INPUT_EVENT_TRANSLATOR, payload, session);
    }
    
    public void publishOutputMessageEvent(Payload payload, WebSocketSession session) {
        this.messageOutputRingBuffer.publishEvent(MESSAGE_INPUT_EVENT_TRANSLATOR, payload, session);
    }
    
    private void startDisruptors() {
        LOG.info("Starting disruptors...");
        
        this.pushEventDisruptor.start();
        this.messageInputEventDisruptor.start();
        this.messageOutputEventDisruptor.start();
        
        this.pushRingBuffer = this.pushEventDisruptor.getRingBuffer();
        this.messageInputRingBuffer = this.messageInputEventDisruptor.getRingBuffer();
        this.messageOutputRingBuffer = this.messageOutputEventDisruptor.getRingBuffer();
        
        LOG.info("Disruptors started");
    }
}

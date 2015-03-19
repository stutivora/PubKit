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

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import com.roquito.platform.notification.ApnsPushNotification;
import com.roquito.platform.notification.GcmPushNotification;
import com.roquito.platform.notification.PushEvent;
import com.roquito.platform.notification.PushEventFactory;
import com.roquito.platform.notification.PushEventHandler;
import com.roquito.platform.notification.PushEventProducer;

/**
 * Created by puran
 */
@Service
public class QueueService {
    
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);
    
    private Disruptor<PushEvent> pushEventDisruptor;
    
    private PushEventProducer pushEventProducer;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private AppDeviceService appDeviceService;
    
    @Autowired
    private UserService userService;
    
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
        
        // Construct the Disruptor
        pushEventDisruptor = new Disruptor<>(pushEventFactory, bufferSize, executor, ProducerType.SINGLE,
                new BlockingWaitStrategy());
        
        // Connect the handler
        EventHandlerGroup<PushEvent> handlerGroup = pushEventDisruptor.handleEventsWith(new PushEventHandler(
                applicationService, appDeviceService));
        if (handlerGroup == null) {
            
        }
        // Start the Disruptor, starts all threads running
        pushEventDisruptor.start();
        
        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<PushEvent> ringBuffer = pushEventDisruptor.getRingBuffer();
        
        // Get the ring buffer from the Disruptor to be used for publishing.
        pushEventProducer = new PushEventProducer(ringBuffer);
       
        LOG.info("Push service initialized");
    }
    
    public void sendGcmPushNotification(GcmPushNotification gcmNotification) {
        pushEventProducer.publishGcmPushNotification(gcmNotification);
    }
    
    public void sendApnsPushNotification(ApnsPushNotification apnsNotification) {
        pushEventProducer.publishApnsPushNotification(apnsNotification);
    }
  
}

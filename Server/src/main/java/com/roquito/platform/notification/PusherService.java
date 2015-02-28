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
package com.roquito.platform.notification;

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
import com.roquito.platform.service.ApplicationService;
import com.roquito.platform.service.UserService;

/**
 * Created by puran
 */
@Service
public class PusherService {
    
    private static final Logger LOG = LoggerFactory.getLogger(PusherService.class);
    
    private Disruptor<PushEvent> pushEventDisruptor;
    private PushEventProducer pushEventProducer;
    private boolean pusherRunning;
    
    @Autowired
    private ApplicationService applicationService;
    
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
                applicationService));
        if (handlerGroup == null) {
            
        }
        // Start the Disruptor, starts all threads running
        pushEventDisruptor.start();
        
        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<PushEvent> ringBuffer = pushEventDisruptor.getRingBuffer();
        
        // Get the ring buffer from the Disruptor to be used for publishing.
        pushEventProducer = new PushEventProducer(ringBuffer);
        
        pusherRunning = true;
        LOG.info("Push service initialized");
    }
    
    public void sendGcmPushNotification(GcmNotification gcmNotification) {
        pushEventProducer.publishGcmPushNotification(gcmNotification);
    }
    
    public void sendApnsPushNotification(ApnsNotification apnsNotification) {
        pushEventProducer.publishApnsPushNotification(apnsNotification);
    }
    
    /**
     * @return the pusherRunning
     */
    public boolean isPusherRunning() {
        return pusherRunning;
    }
    
}

package com.roquito.platform.notification;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Created by puran on 1/30/15.
 */
public class PusherService {

    private static PusherService INSTANCE;

    private Disruptor<PushEvent> pushEventDisruptor;
    private PushEventProducer pushEventProducer;
    private boolean pusherRunning;

    public static PusherService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PusherService();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public void start() {
        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // The factory for the event
        PushEventFactory pushEventFactory = new PushEventFactory();

        // Construct the Disruptor
        pushEventDisruptor = new Disruptor<>(pushEventFactory, bufferSize, executor,
                ProducerType.SINGLE, new BlockingWaitStrategy());

        // Connect the handler
        EventHandlerGroup<PushEvent> handlerGroup = pushEventDisruptor.handleEventsWith(new PushEventHandler());
        if (handlerGroup == null) {
            
        }
        // Start the Disruptor, starts all threads running
        pushEventDisruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<PushEvent> ringBuffer = pushEventDisruptor.getRingBuffer();

        // Get the ring buffer from the Disruptor to be used for publishing.
        pushEventProducer = new PushEventProducer(ringBuffer);

        pusherRunning = true;
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

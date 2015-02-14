package com.roquito.platform.notification;

import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;

/**
 * Created by puran on 1/30/15.
 */
public class PushEventProducer {

    private final RingBuffer<PushEvent> ringBuffer;

    public PushEventProducer(RingBuffer<PushEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorTwoArg<PushEvent, PushNotification, PushType> PUSH_EVENT_TRANSLATOR =
            new EventTranslatorTwoArg<PushEvent, PushNotification, PushType>() {
                @Override
                public void translateTo(PushEvent event, long sequence, PushNotification gcmNotification, PushType pushType) {
                    event.setPushNotification(gcmNotification);
                    event.setPushType(pushType);
                }
            };

    public void publishGcmPushNotification(GcmNotification gcmNotification) {
        ringBuffer.publishEvent(PUSH_EVENT_TRANSLATOR, gcmNotification, PushType.GCM);
    }

    public void publishApnsPushNotification(ApnsNotification apnsNotification) {
        ringBuffer.publishEvent(PUSH_EVENT_TRANSLATOR, apnsNotification, PushType.APNS);
    }
}

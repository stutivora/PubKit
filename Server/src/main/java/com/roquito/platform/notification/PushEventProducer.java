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

import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;

/**
 * Created by puran
 */
public class PushEventProducer {
    
    private final RingBuffer<PushEvent> ringBuffer;
    
    public PushEventProducer(RingBuffer<PushEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
    
    private static final EventTranslatorTwoArg<PushEvent, PushNotification, PushType> PUSH_EVENT_TRANSLATOR = new EventTranslatorTwoArg<PushEvent, PushNotification, PushType>() {
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

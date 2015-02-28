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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.roquito.platform.model.Application;
import com.roquito.platform.notification.gcm.GcmConnection;
import com.roquito.platform.notification.gcm.Message;
import com.roquito.platform.notification.gcm.MulticastResult;
import com.roquito.platform.notification.gcm.Result;
import com.roquito.platform.service.ApplicationService;

/**
 * Created by puran
 */
public class PushEventHandler implements EventHandler<PushEvent> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PushEventHandler.class);
    
    private static final int NUM_RETRIES = 3;
    private ApplicationService applicationService;
    
    public PushEventHandler(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    @Override
    public void onEvent(PushEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Processed Event: " + event + " payload:" + event.getPushNotification());
        
        switch (event.getPushType()) {
            case GCM:
                handleGCMPush(event.getPushNotification());
                break;
            case APNS:
                break;
            default:
                break;
        }
    }
    
    private void handleGCMPush(PushNotification pushNotification) {
        if (pushNotification == null) {
            LOG.debug("Empty or null push notification data received. Could not send notification to GCM server");
            return;
        }
        GcmNotification gcmNotification = (GcmNotification) pushNotification;
        Application roquitoApplication = applicationService.findByApplicationId(gcmNotification.getApplicationId());
        if (roquitoApplication == null) {
            LOG.debug("Couldn't find roquito application");
            return;
        }
        String gcmApiKey = roquitoApplication.getAndroidGCMKey();
        if (gcmApiKey == null) {
            LOG.debug("GCM API key not available to send push notification to GCM server");
            return;
        }
        GcmConnection gcmConnection = new GcmConnection(gcmApiKey);
        Message gcmMessage = constructGcmMessage(gcmNotification);
        try {
            if (gcmNotification.isMulticast() && gcmNotification.getRegistrationIds().size() > 0) {
                MulticastResult multicastResult = null;
                if (gcmNotification.isRetry()) {
                    multicastResult = gcmConnection.send(gcmMessage, gcmNotification.getRegistrationIds(), NUM_RETRIES);
                } else {
                    multicastResult = gcmConnection.sendNoRetry(gcmMessage, gcmNotification.getRegistrationIds());
                }
                handleMulticastResult(multicastResult);
            } else {
                Result result = null;
                if (gcmNotification.isRetry()) {
                    result = gcmConnection.send(gcmMessage, gcmNotification.getRegistrationIds().get(0), NUM_RETRIES);
                } else {
                    result = gcmConnection.sendNoRetry(gcmMessage, gcmNotification.getRegistrationIds().get(0));
                }
                handleGCMResult(result);
            }
        } catch (Exception es) {
            LOG.error("Error sending push notification to GCM server", es);
        }
    }
    
    private void handleMulticastResult(MulticastResult multicastResult) {
        if (multicastResult == null) {
            LOG.error("Error sending multicast messages to GCM server. Null response received");
            return;
        }
        for (Result result : multicastResult.getResults()) {
            handleGCMResult(result);
        }
    }
    
    private void handleGCMResult(Result result) {
        if (result == null) {
            LOG.error("Error sending message to GCM server. Null response received");
            return;
        }
        if (result.getMessageId() == null) {
            String errorCode = result.getErrorCodeName();
            LOG.error("Error sending message to GCM server. Error code:" + errorCode);
        } else {
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // TODO: update server data store with new regId.
            }
            LOG.info("GCM message sent with message id:" + result.getMessageId());
        }
    }
    
    private Message constructGcmMessage(GcmNotification gcmNotification) {
        Message.Builder messageBuilder = new Message.Builder();
        
        Map<String, String> pushData = gcmNotification.getData();
        
        for (Map.Entry<String, String> entry : pushData.entrySet()) {
            messageBuilder.addData(entry.getKey(), entry.getValue());
        }
        if (gcmNotification.getCollapseKey() != null) {
            messageBuilder.collapseKey(gcmNotification.getCollapseKey());
        }
        messageBuilder.timeToLive(gcmNotification.getTimeToLive());
        messageBuilder.delayWhileIdle(gcmNotification.isDelayWhileIdle());
        
        return messageBuilder.build();
    }
}

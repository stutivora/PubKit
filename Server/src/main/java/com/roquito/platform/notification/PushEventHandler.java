package com.roquito.platform.notification;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.roquito.platform.notification.gcm.GcmConnection;
import com.roquito.platform.notification.gcm.Message;
import com.roquito.platform.notification.gcm.MulticastResult;
import com.roquito.platform.notification.gcm.Result;
import com.roquito.platform.model.Application;
import com.roquito.platform.service.ApplicationService;

/**
 * Created by puran on 1/30/15.
 */
public class PushEventHandler implements EventHandler<PushEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(PushEventHandler.class);
    private static final int NUM_RETRIES = 3;
    private ApplicationService applicationService;

    public PushEventHandler() {
        applicationService = new ApplicationService();
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
            log.debug("Empty or null push notification data received. Could not send notification to GCM server");
            return;
        }
        GcmNotification gcmNotification = (GcmNotification) pushNotification;
        Application roquitoApplication = applicationService.findByApplicationId(gcmNotification.getApplicationId());
        if (roquitoApplication == null) {
            log.debug("Couldn't find roquito application");
            return;
        }
        String gcmApiKey = roquitoApplication.getAndroidGCMKey();
        if (gcmApiKey == null) {
            log.debug("GCM API key not available to send push notification to GCM server");
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
            log.error("Error sending push notification to GCM server", es);
        }
    }

    private void handleMulticastResult(MulticastResult multicastResult) {
        if (multicastResult == null) {
            log.error("Error sending multicast messages to GCM server. Null response received");
            return;
        }
        for (Result result : multicastResult.getResults()) {
            handleGCMResult(result);
        }
    }

    private void handleGCMResult(Result result) {
        if (result == null) {
            log.error("Error sending message to GCM server. Null response received");
            return;
        }
        if (result.getMessageId() == null) {
            String errorCode = result.getErrorCodeName();
            log.error("Error sending message to GCM server. Error code:" + errorCode);
        } else {
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                //TODO: update server data store with new regId.
            }
            log.info("GCM message sent with message id:" + result.getMessageId());
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

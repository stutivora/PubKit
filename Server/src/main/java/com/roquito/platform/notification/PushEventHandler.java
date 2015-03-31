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

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.EnhancedApnsNotification;
import com.notnoop.exceptions.NetworkIOException;
import com.roquito.platform.model.DeviceInfo;
import com.roquito.platform.model.Application;
import com.roquito.platform.notification.gcm.GcmConnection;
import com.roquito.platform.notification.gcm.Message;
import com.roquito.platform.notification.gcm.MulticastResult;
import com.roquito.platform.notification.gcm.Result;
import com.roquito.platform.service.DeviceInfoService;
import com.roquito.platform.service.ApplicationService;

/**
 * Created by puran
 */
public class PushEventHandler implements EventHandler<PushEvent> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PushEventHandler.class);
    
    private static final int NUM_RETRIES = 3;
    private ApplicationService applicationService;
    private DeviceInfoService deviceInfoService;
    
    private Map<String, ApnsService> sandboxConnections = new HashMap<String, ApnsService>();
    private Map<String, ApnsService> prodConnections = new HashMap<String, ApnsService>();
    
    public PushEventHandler(ApplicationService applicationService, DeviceInfoService deviceInfoService) {
        this.applicationService = applicationService;
        this.deviceInfoService = deviceInfoService;
    }
    
    @Override
    public void onEvent(PushEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Processed Event: " + event + " payload:" + event.getPushNotification());
        
        switch (event.getPushType()) {
            case GCM:
                handleGCMPush(event.getPushNotification());
                break;
            case APNS:
                handleAPNSPush(event.getPushNotification());
                break;
            default:
                break;
        }
    }
    
    private void handleAPNSPush(PushNotification pushNotification) {
        if (pushNotification == null) {
            LOG.debug("Empty or null push notification data received. Could not send notification to APNS server");
            return;
        }
        ApnsPushNotification apnsPushNotification = (ApnsPushNotification) pushNotification;
        Application application = applicationService
                .findByApplicationId(apnsPushNotification.getApplicationId());
        if (application == null) {
            LOG.debug("Couldn't find roquito application");
            return;
        }
        
        ApnsService apnsService = null;
        boolean isProduction = apnsPushNotification.isProductionMode();
        if (isProduction) {
            apnsService = this.prodConnections.get(apnsPushNotification.getApplicationId());
        } else {
            apnsService = this.sandboxConnections.get(apnsPushNotification.getApplicationId());
        }
        if (apnsService == null) {
            apnsService = createNewConnection(application, isProduction);
            if (apnsService == null) {
                LOG.debug("Error creating apns service. Check if required certs and password is set for this application");
                return;
            }
        }
        
        if (testApnsConnection(apnsService)) {
            //try to create new connection
            apnsService = createNewConnection(application, isProduction);
            
            if (apnsPushNotification.getDeviceTokens().size() == 0) {
                LOG.debug("Error sending push, no device tokens");
            } else if (apnsPushNotification.getDeviceTokens().size() == 1) {
                sendSingleApnsPush(apnsService, apnsPushNotification);
            } else {
                sendMultipleApnsPush(apnsService, apnsPushNotification);
            }
            
            if (apnsPushNotification.isNeedFeedback()) {
                String applicationId = apnsPushNotification.getApplicationId();
                Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
                for (String deviceToken : inactiveDevices.keySet()) {
                    DeviceInfo deviceInfo = deviceInfoService.getDeviceInfoForToken(applicationId, deviceToken);
                    if (deviceInfo != null) {
                        deviceInfo.setActive(false);
                        deviceInfoService.saveDeviceInfo(deviceInfo);
                    }
                }
            }
        }
    }
    
    private ApnsService createNewConnection(Application application, boolean isProduction) {
        String certFileId = application.getApnsCertFileId(isProduction);
        InputStream certFileStream = applicationService.getFileAsStream(certFileId);
        
        ApnsService apnsService = APNS.newService()
                .withCert(certFileStream, application.getApnsCertPassword(isProduction))
                .withAppleDestination(isProduction).build();
        
        if (isProduction) {
            this.prodConnections.put(application.getApplicationId(), apnsService);
        } else {
            this.sandboxConnections.put(application.getApplicationId(), apnsService);
        }
        return apnsService;
    }
    
    private boolean testApnsConnection(ApnsService apnsService) {
        try {
            apnsService.testConnection();
            return true;
        } catch (NetworkIOException es) {
            LOG.error("APNS connection failed", es);
        }
        return false;
    }
    
    private void sendSingleApnsPush(ApnsService apnsService, ApnsPushNotification pushNotification) {
        String deviceToken = pushNotification.getDeviceTokens().get(0);
        try {
            if (pushNotification.isNeedFeedback()) {
                int now = (int) (new Date().getTime() / 1000);
                EnhancedApnsNotification notification = new EnhancedApnsNotification(
                        EnhancedApnsNotification.INCREMENT_ID(), now + 60 * 60, deviceToken,
                        pushNotification.getPayload());
                apnsService.push(notification);
            } else {
                ApnsNotification resultNotification = apnsService.push(deviceToken, pushNotification.getPayload());
                LOG.info("Push notification sent to device with device token: " + resultNotification.getDeviceToken());
            }
        } catch (NetworkIOException networkException) {
            LOG.error("Error sending apple push notification for device token:" + deviceToken);
        }
    }
    
    private void sendMultipleApnsPush(ApnsService apnsService, ApnsPushNotification pushNotification) {
        try {
            if (pushNotification.isNeedFeedback()) {
                int expiry = (int) (new Date().getTime() / 1000) + 60 + 60;
                Collection<? extends EnhancedApnsNotification> results = apnsService.push(
                        pushNotification.getDeviceTokens(), pushNotification.getPayload(), new Date(expiry));
                if (results == null) {
                    LOG.error("Error sending bulk apple push notification, null results returned");
                } else {
                    LOG.info("Bulk apple push notification sent");
                }
            } else {
                Collection<? extends ApnsNotification> results = apnsService.push(pushNotification.getDeviceTokens(),
                        pushNotification.getPayload());
                if (results == null) {
                    LOG.error("Error sending bulk apple push notification, null results returned");
                } else {
                    LOG.info("Bulk apple push notification sent");
                }
            }
        } catch (NetworkIOException ne) {
            LOG.error("Error sending bulk apple push notification", ne);
        }
    }
    
    private void handleGCMPush(PushNotification pushNotification) {
        if (pushNotification == null) {
            LOG.debug("Empty or null push notification data received. Could not send notification to GCM server");
            return;
        }
        GcmPushNotification gcmNotification = (GcmPushNotification) pushNotification;
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
    
    private Message constructGcmMessage(GcmPushNotification gcmNotification) {
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

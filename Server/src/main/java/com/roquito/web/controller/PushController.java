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
package com.roquito.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.roquito.platform.model.DataConstants;
import com.roquito.platform.model.DeviceInfo;
import com.roquito.platform.notification.BroadcastNotification;
import com.roquito.platform.notification.SimpleApnsPushNotification;
import com.roquito.platform.notification.SimpleGcmPushNotification;
import com.roquito.platform.service.DeviceInfoService;
import com.roquito.platform.service.QueueService;
import com.roquito.web.data.DeviceInfoData;
import com.roquito.web.data.SimpleGcmPushData;
import com.roquito.web.exception.RoquitoServerException;
import com.roquito.web.response.ApiResponse;
import com.roquito.web.response.DeviceRegistrationResponse;

/**
 * This is a public API for sending push notification. This API doesn't return
 * or block the caller for the actual push response. It just puts the
 * notification request to the queue and returns.
 * 
 * Created by puran
 */
@RestController
@RequestMapping("/api/push")
public class PushController extends BaseController {
    
    private static final Logger LOG = LoggerFactory.getLogger(PushController.class);
    
    @Autowired
    private QueueService pusherService;
    
    @Autowired
    private DeviceInfoService deviceInfoService;
    
    @RequestMapping(value = "/gcm", method = RequestMethod.POST)
    public String create(@RequestBody SimpleGcmPushNotification gcmNotification) {
        if (gcmNotification == null) {
            LOG.debug("Null gcm notification data received");
            new RoquitoServerException("Invalid request");
        }
        String applicationId = gcmNotification.getApplicationId();
        validateApiRequest(applicationId);
        
        pusherService.sendGcmPushNotification(gcmNotification);
        
        return "OK";
    }
    
    @RequestMapping(value = "/apns", method = RequestMethod.POST)
    public String create(@RequestBody SimpleApnsPushNotification apnsNotification) {
        if (apnsNotification == null) {
            LOG.debug("Null apns notification data received");
            new RoquitoServerException("Invalid request");
        }
        String applicationId = apnsNotification.getApplicationId();
        validateApiRequest(applicationId);
        
        pusherService.sendApnsPushNotification(apnsNotification);
        LOG.info("Added APNS notification message to the pusher queue");
        
        return "OK";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public DeviceRegistrationResponse registerDevice(@RequestBody DeviceInfoData deviceInfoData) {
        LOG.info("Device registration request received for push notification");
        
        if (deviceInfoData == null || isEmpty(deviceInfoData.getDeviceType())
                || isEmpty(deviceInfoData.getApplicationId())) {
            LOG.debug("Null or invalid data received");
            new DeviceRegistrationResponse("Invalid request");
        }
        if (DataConstants.DEVICE_TYPE_IOS.equals(deviceInfoData.getDeviceType())
                && isEmpty(deviceInfoData.getDeviceToken())) {
            LOG.debug("invalid device token received");
            new DeviceRegistrationResponse("Invalid request");
        }
        if (DataConstants.DEVICE_TYPE_ANDROID.equals(deviceInfoData.getDeviceType())
                && isEmpty(deviceInfoData.getRegistrationId())) {
            LOG.debug("invalid registration id received");
            new DeviceRegistrationResponse("Invalid request");
        }
        
        String applicationId = deviceInfoData.getApplicationId();
        validateApiRequest(applicationId);
        
        DeviceInfo deviceInfo = null;
        boolean newInfo = false;
        if (hasValue(deviceInfoData.getDeviceInfoId())) {
            deviceInfo = deviceInfoService.get(deviceInfoData.getDeviceInfoId());
        } else if (DataConstants.DEVICE_TYPE_IOS.equals(deviceInfoData.getDeviceType())) {
            deviceInfo = deviceInfoService.getDeviceInfoForToken(applicationId, deviceInfoData.getDeviceToken());
        } else if (DataConstants.DEVICE_TYPE_ANDROID.equals(deviceInfoData.getDeviceType())) {
            deviceInfo = deviceInfoService.getDeviceInfoForRegistrationId(applicationId,
                    deviceInfoData.getRegistrationId());
        } else {
            deviceInfo = new DeviceInfo();
            newInfo = true;
        }
        
        deviceInfo.setApplicationId(applicationId);
        deviceInfo.setSourceUserId(deviceInfoData.getSourceUserId());
        
        if (hasValue(deviceInfoData.getDeviceToken())) {
            deviceInfo.setDeviceToken(deviceInfoData.getDeviceToken());
        }
        if (hasValue(deviceInfoData.getRegistrationId())) {
            deviceInfo.setRegistrationId(deviceInfoData.getRegistrationId());
        }
        
        deviceInfo.setDeviceType(deviceInfoData.getDeviceType());
        deviceInfo.setDeviceSubType(deviceInfoData.getDeviceSubType());
        deviceInfo.setActive(true);
        
        String deviceInfoId = deviceInfoService.saveDeviceInfo(deviceInfo);
        if (deviceInfoId != null) {
            if (newInfo) {
                LOG.info("Registered new device info for push notification");
            } else {
                LOG.info("Updated device info for push notification");
            }
            return new DeviceRegistrationResponse(deviceInfoId, false, null);
        }
        
        LOG.error("Error registering device for push notification");
        return null;
    }
    
    @RequestMapping(value = "/broadcast/gcm", method = RequestMethod.POST)
    public ApiResponse sendBroadcastGcmPush(@RequestBody SimpleGcmPushData gcmPushData) {
        LOG.info("Received simple gcm push notification request for application {" + gcmPushData.getApplicationId()
                + "}");
        
        validateApiRequest(gcmPushData.getApplicationId());
        
        BroadcastNotification broadcastNotification = new BroadcastNotification();
        broadcastNotification.setApplicationId(gcmPushData.getApplicationId());
        broadcastNotification.setData(gcmPushData.getData());
        broadcastNotification.setDeviceType(DataConstants.DEVICE_TYPE_ANDROID);
        
        queueService.sendBroadcastPushNotification(broadcastNotification);
        
        LOG.warn("Gcm broadcast push notification added to the queue for application {"
                + gcmPushData.getApplicationId() + "}");
        
        return new ApiResponse("Push broadcast added to the queue", false, null);
    }
    
    @RequestMapping(value = "/simple/gcm", method = RequestMethod.POST)
    public ApiResponse sendSimpleGcmPush(@RequestBody SimpleGcmPushData gcmPushData) {
        LOG.info("Received simple gcm push notification request for application {" + gcmPushData.getApplicationId()
                + "}");
        validateApiRequest(gcmPushData.getApplicationId());
        
        if (isEmpty(gcmPushData.getSourceUserId()) && isEmpty(gcmPushData.getRegistrationId())) {
            LOG.error("Invalid gcm push request {" + gcmPushData.getSourceUserId() + "} for application {"
                    + gcmPushData.getApplicationId() + "}");
            return new ApiResponse(null, true, "Invalid Request");
        }
        
        List<String> registrationIds = new ArrayList<String>();
        if (isEmpty(gcmPushData.getRegistrationId()) && hasValue(gcmPushData.getSourceUserId())) {
            List<DeviceInfo> devices = deviceInfoService.getDeviceInfoForUserId(gcmPushData.getApplicationId(),
                    gcmPushData.getSourceUserId(), DataConstants.DEVICE_TYPE_ANDROID);
            if (devices == null || devices.isEmpty()) {
                LOG.warn("Device info not found for source user info {" + gcmPushData.getSourceUserId()
                        + "} for application {" + gcmPushData.getApplicationId() + "}");
                return new ApiResponse("User or device not found", false, null);
            }
            for (DeviceInfo device : devices) {
                if (hasValue(device.getRegistrationId())) {
                    registrationIds.add(device.getRegistrationId());
                }
            }
        } else {
            registrationIds.add(gcmPushData.getRegistrationId());
        }
        
        SimpleGcmPushNotification gcm = new SimpleGcmPushNotification();
        gcm.setApplicationId(gcmPushData.getApplicationId());
        gcm.setApplicationVersion("1.0");
        gcm.setRegistrationIds(registrationIds);
        
        Map<String, String> data = new HashMap<>();
        data.put("data", gcmPushData.getData());
        gcm.setData(data);
        
        gcm.setMulticast(registrationIds.size() > 1);
        queueService.sendGcmPushNotification(gcm);
        
        LOG.warn("Gcm push notification added to the queue for application {" + gcmPushData.getApplicationId() + "}");
        return new ApiResponse("Push message added to the queue", false, null);
    }
}

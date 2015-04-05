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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.roquito.platform.model.DataConstants;
import com.roquito.platform.model.DeviceInfo;
import com.roquito.platform.service.DeviceInfoService;
import com.roquito.platform.service.QueueService;
/**
 * Broadcast event handler for broadcasting push notification to all the devices
 * for the given application.
 * 
 * @author puran
 */
public class BroadcastEventHandler implements EventHandler<BroadcastEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(BroadcastEventHandler.class);
    
    private QueueService queueService;
    private DeviceInfoService deviceInfoService;
    
    public BroadcastEventHandler(DeviceInfoService deviceInfoService, QueueService queueService) {
        this.queueService = queueService;
        this.deviceInfoService = deviceInfoService;
    }

    @Override
    public void onEvent(BroadcastEvent event, long sequence, boolean endOfBatch) throws Exception {
        
        BroadcastNotification bn = event.getBroadcastNotification();
        LOG.info("Processing broadcast push notification for application id {"+bn.getApplicationId()+"}");
        
        int offset = 0;
        List<DeviceInfo> devices = deviceInfoService.getDevices(bn.getApplicationId(), bn.getDeviceType(), offset, 800);
        while(devices != null && !devices.isEmpty()) {
            if (devices != null && !devices.isEmpty()) {
                if (DataConstants.DEVICE_TYPE_ANDROID.equals(bn.getDeviceType())) {
                    queueGcmNotification(devices, bn);
                } else if (DataConstants.DEVICE_TYPE_IOS.equals(bn.getDeviceType())) {
                    queueApnsNotification(devices, bn);
                } else {
                    //ignore
                }
            } 
            offset = offset + devices.size();
            devices = deviceInfoService.getDevices(bn.getApplicationId(), bn.getDeviceType(), offset, 800);
        }
        LOG.info("Push notification broadcasted to "+ offset + " devices");
    }
    
    private void queueGcmNotification(List<DeviceInfo> devices, BroadcastNotification notification) {
        List<String> registrationIds = new ArrayList<String>();
        for (DeviceInfo deviceInfo : devices) {
            if (deviceInfo.getRegistrationId() != null) {
                registrationIds.add(deviceInfo.getRegistrationId());
            }
        }
        SimpleGcmPushNotification gcm = new SimpleGcmPushNotification();
        gcm.setApplicationId(notification.getApplicationId());

        //TODO: Hard code till when??
        gcm.setApplicationVersion("1.0");
        
        Map<String, String> data = new HashMap<>();
        data.put("data", notification.getData());
        gcm.setData(data);
        
        gcm.setRegistrationIds(registrationIds);
        
        gcm.setMulticast(true);
        gcm.setRetry(false);
        
        queueService.sendGcmPushNotification(gcm);
    }
    
    private void queueApnsNotification(List<DeviceInfo> devices, BroadcastNotification notification) {
        List<String> deviceTokens = new ArrayList<String>();
        for (DeviceInfo deviceInfo : devices) {
            if (deviceInfo.getDeviceToken() != null) {
                deviceTokens.add(deviceInfo.getDeviceToken());
            }
        }
        SimpleApnsPushNotification apns = new SimpleApnsPushNotification();
        apns.setApplicationId(notification.getApplicationId());
        apns.setApplicationId("1.0");
        apns.setAlert(notification.getData());
        apns.setDeviceTokens(deviceTokens);
        apns.setProductionMode(true);
        
        queueService.sendApnsPushNotification(apns);
    }
}

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
        LOG.info("Processing broadcast push notification");
        BroadcastNotification bn = event.getBroadcastNotification();
        
        int offset = 0;
        List<DeviceInfo> devices = null;
        
        while(devices != null && !devices.isEmpty()) {
            devices = deviceInfoService.getDevices(bn.getApplicationId(), bn.getDeviceType(), offset, 800);
            if (devices != null && !devices.isEmpty()) {
                if (DataConstants.DEVICE_TYPE_ANDROID.equals(bn.getDeviceType())) {
                    GcmPushNotification gcm = new GcmPushNotification();
                    gcm.setApplicationId(bn.getApplicationId());
                    //TODO: Hard code till when??
                    gcm.setApplicationVersion("1.0");
                    
                    List<String> registrationIds = new ArrayList<String>();
                    for (DeviceInfo deviceInfo : devices) {
                        if (deviceInfo.getRegistrationId() != null) {
                            registrationIds.add(deviceInfo.getRegistrationId());
                        }
                    }
                    Map<String, String> data = new HashMap<>();
                    data.put("data", bn.getData());
                    gcm.setData(data);
                    
                    gcm.setMulticast(true);
                    gcm.setRetry(false);
                    
                    queueService.sendGcmPushNotification(gcm);
                }
                
                if (DataConstants.DEVICE_TYPE_IOS.equals(bn.getDeviceType())) {
                    
                }
            } 
            offset = offset + devices.size();
        }
        LOG.info("Push notification broadcasted to "+ offset + " devices");
    }
}

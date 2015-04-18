package com.pubkit.platform.service.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pubkit.platform.model.DeviceInfo;
import com.pubkit.platform.persistence.DeviceInfoDao;
import com.pubkit.platform.persistence.SequenceDao;
import com.pubkit.platform.service.DeviceInfoService;
/**
 * Created by puran
 */
@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DeviceInfoService.class);
    
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    
    @Autowired 
    private SequenceDao sequenceDao;

    @Override
    public String saveDeviceInfo(DeviceInfo deviceInfo) {
        deviceInfoDao.save(deviceInfo);
        return deviceInfo.getId().toString();
    }
    
    @Override
    public DeviceInfo getDevice(String deviceInfoId) {
        if (deviceInfoId == null) {
            return null;
        }
        return deviceInfoDao.getDeviceInfo(deviceInfoId);
    }

    @Override
    public DeviceInfo getDeviceInfoForRegistrationId(String applicationId, String registrationId) {
        return deviceInfoDao.getDeviceInfoForRegistrationId(applicationId, registrationId);
    }

    @Override
    public DeviceInfo getDeviceInfoForToken(String applicationId, String deviceToken) {
        return deviceInfoDao.getDeviceInfoForToken(applicationId, deviceToken);
    }

    @Override
    public List<DeviceInfo> getDeviceInfoForUserId(String applicationId, String userId, String deviceType) {
        List<DeviceInfo> results = deviceInfoDao.getDeviceInfoForUserId(applicationId, userId, deviceType);
        if (results != null) {
            return results;
        } else {
            LOG.info("App device not found for source user id {" + userId + "} application id {" + applicationId + "}");
            return null;
        }
    }

    @Override
    public List<DeviceInfo> getDevices(String applicationId, String deviceType, int offset, int limit) {
        List<DeviceInfo> results = deviceInfoDao.getDevices(applicationId, deviceType, offset, limit);
        if (results != null) {
            return results;
        } else {
            LOG.info("No devices for application:" + applicationId + " of deviceType {" + deviceType +"}");
            return Collections.emptyList();
        }
    }
    
}

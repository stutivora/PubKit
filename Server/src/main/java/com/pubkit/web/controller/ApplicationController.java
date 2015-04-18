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
package com.pubkit.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pubkit.platform.model.Application;
import com.pubkit.platform.model.DataConstants;
import com.pubkit.platform.model.User;
import com.pubkit.web.data.AppConfigData;
import com.pubkit.web.data.ApplicationData;
import com.pubkit.web.exception.RoquitoServerException;
import com.pubkit.web.response.ApiResponse;
import com.pubkit.web.response.ApplicationResponse;

/**
 * Created by puran
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController extends BaseController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);
    
    @RequestMapping(method = RequestMethod.POST)
    public ApplicationResponse create(@RequestBody ApplicationData application) {
        
        validateAccessToken();
        
        if (application == null || isEmpty(application.getUserId()) || isEmpty(application.getApplicationName())) {
            LOG.debug("Missing application data for creating new application");
            return new ApplicationResponse("Missing required data");
        }
        
        User owner = userService.findByUserId(application.getUserId());
        if (owner == null) {
            LOG.debug("Missing owner data, required for registering application");
            throw new RoquitoServerException("Owner required. Error creating new application");
        }
        Application savedApplication = applicationService.findByApplicationName(application.getApplicationName());
        if (savedApplication != null) {
            LOG.debug("Cannot register application. ApplicationData with same name already exists:"
                    + application.getApplicationName());
            return new ApplicationResponse("ApplicationData with same name already exists");
        }
        
        Application newApplication = new Application();
        newApplication.setApplicationName(application.getApplicationName());
        
        String applicationId = applicationService.getNextId(Application.class);
        newApplication.setApplicationId(applicationId);
        
        String applicationKey = keyGenerator.getRandomKey();
        newApplication.setApplicationKey(applicationKey);
        
        String applicationSecret = keyGenerator.getSecureSessionId();
        newApplication.setApplicationSecret(applicationSecret);
        
        newApplication.setApplicationDescription(application.getApplicationDescription());
        newApplication.setWebsiteLink(application.getWebsiteLink());
        newApplication.setPricingPlan(application.getPricingPlan());
        
        newApplication.setOwner(owner);
        // Create with empty config
        newApplication.setConfigParams(new HashMap<String, String>());
        
        newApplication.setCreatedDate(new Date());
        
        applicationService.saveApplication(newApplication);
        LOG.info("ApplicationData registered with name:" + newApplication.getApplicationName());
        
        application.setApplicationId(applicationId);
        application.setApplicationKey(applicationKey);
        application.setApplicationSecret(applicationSecret);
        
        return new ApplicationResponse(application);
    }
    
    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ApiResponse updateAppConfig(@RequestBody AppConfigData configDto) {
        validateAccessToken();
        
        if (configDto == null) {
            LOG.debug("Missing application data for creating application config");
            return new ApiResponse(null, true, "Missing required data");
        }
        Application savedApplication = applicationService.findByApplicationId(configDto.getApplicationId());
        if (savedApplication == null) {
            throw new RoquitoServerException("Wrong application identifier");
        }
        
        Map<String, String> appConfigs = savedApplication.getConfigParams();
        if (appConfigs == null) {
            appConfigs = new HashMap<>();
        }
        appConfigs.put(DataConstants.ANDROID_GCM_KEY, configDto.getAndroidGCMKey());
        
        appConfigs.put(DataConstants.APNS_DEV_CERT_FILE_ID, configDto.getApnsDevCertFileId());
        appConfigs.put(DataConstants.APNS_DEV_CERT_FILE_NAME, configDto.getApnsDevCertFileName());
        appConfigs.put(DataConstants.APNS_DEV_CERT_PASSWORD, configDto.getApnsDevCertPassword());
        
        appConfigs.put(DataConstants.APNS_PROD_CERT_FILE_ID, configDto.getApnsProdCertFileId());
        appConfigs.put(DataConstants.APNS_PROD_CERT_FILE_NAME, configDto.getApnsProdCertFileName());
        appConfigs.put(DataConstants.APNS_PROD_CERT_PASSWORD, configDto.getApnsProdCertPassword());
        
        savedApplication.setConfigParams(appConfigs);
        
        applicationService.saveApplication(savedApplication);
        return new ApiResponse("Config saved", false, null);
    }
    
    @RequestMapping(value = "{applicationId}", method = RequestMethod.GET)
    public ApplicationResponse getApplication(@PathVariable("applicationId") String applicationId) {
        validateAccessToken();
        
        Application application = applicationService.findByApplicationId(applicationId);
        if (application != null) {
            ApplicationData appData = getApplicationData(application, true);
            return new ApplicationResponse(appData);
        } else {
            return new ApplicationResponse("ApplicationData not found");
        }
    }
}

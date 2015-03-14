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

import com.roquito.platform.model.Application;
import com.roquito.platform.model.DataConstants;
import com.roquito.platform.model.User;
import com.roquito.web.dto.AppConfigDto;
import com.roquito.web.dto.ApplicationDto;
import com.roquito.web.exception.RoquitoServerException;
import com.roquito.web.response.ApplicationResponse;
import com.roquito.web.response.ConfigResponse;

/**
 * Created by puran
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController extends BaseController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);
    
    @RequestMapping(method = RequestMethod.POST)
    public ApplicationResponse create(@RequestBody ApplicationDto applicationDto) {
        
        validateAccessToken();
        
        if (applicationDto == null || isEmpty(applicationDto.getUserId())
                || isEmpty(applicationDto.getApplicationName())) {
            LOG.debug("Missing application data for creating new application");
            return new ApplicationResponse("Missing required data");
        }
        
        User owner = userService.findByUserId(applicationDto.getUserId());
        if (owner == null) {
            LOG.debug("Missing owner data, required for registering application");
            throw new RoquitoServerException("Owner required. Error creating new application");
        }
        Application savedApplication = applicationService.findByApplicationName(applicationDto.getApplicationName());
        if (savedApplication != null) {
            LOG.debug("Cannot register application. Application with same name already exists:"
                    + applicationDto.getApplicationName());
            return new ApplicationResponse("Application with same name already exists");
        }
        
        Application newApplication = new Application();
        newApplication.setApplicationName(applicationDto.getApplicationName());
        
        String applicationId = applicationService.getNextApplicationId();
        newApplication.setApplicationId(applicationId);
        
        String applicationKey = keyGenerator.getRandomKey();
        newApplication.setApplicationKey(applicationKey);
        
        String applicationSecret = keyGenerator.getSecureSessionId();
        newApplication.setApplicationSecret(applicationSecret);
        
        newApplication.setApplicationDescription(applicationDto.getApplicationDescription());
        newApplication.setWebsiteLink(applicationDto.getWebsiteLink());
        newApplication.setPricingPlan(applicationDto.getPricingPlan());
        
        newApplication.setOwner(owner);
        //Create with empty config
        newApplication.setConfigParams(new HashMap<String, String>());
        
        newApplication.setCreatedDate(new Date());
        
        String internalId = applicationService.saveApplication(newApplication);
        if (internalId != null) {
            LOG.info("Application registered with name:" + newApplication.getApplicationName());
            
            applicationDto.setApplicationId(applicationId);
            applicationDto.setApplicationKey(applicationKey);
            applicationDto.setApplicationSecret(applicationSecret);
            
            return new ApplicationResponse(applicationDto);
        }
        throw new RoquitoServerException("Error creating new application. Try again later.");
    }
    
    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ConfigResponse updateAppConfig(@RequestBody AppConfigDto configDto) {
        validateAccessToken();
        
        if (configDto == null) {
            LOG.debug("Missing application data for creating application config");
            return new ConfigResponse(null, true, "Missing required data");
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
        
        String savedId = applicationService.saveApplication(savedApplication);
        if (savedId == null) {
            LOG.error("Error updating application configuration");
            return new ConfigResponse(null, true, "Error saving configuration");
        }
        return new ConfigResponse("Config saved",false, null);
    }
    
    @RequestMapping(value = "{applicationId}", method = RequestMethod.GET)
    public ApplicationResponse getApplication(@PathVariable("applicationId") String applicationId) {
        validateAccessToken();
        
        Application application = applicationService.findByApplicationId(applicationId);
        if (application != null) {
            ApplicationDto appDto = getApplicationDto(application, true);
            return new ApplicationResponse(appDto);
        } else {
            return new ApplicationResponse("Application not found");
        }
    }
}

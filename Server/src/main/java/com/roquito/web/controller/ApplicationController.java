package com.roquito.web.controller;

import com.roquito.platform.model.Application;
import com.roquito.platform.model.ApplicationConfig;
import com.roquito.platform.model.DataConstants;
import com.roquito.platform.model.User;
import com.roquito.web.dto.AppConfigDto;
import com.roquito.web.dto.ApplicationDto;
import com.roquito.web.exception.RoquitoServerException;
import com.roquito.web.response.ApplicationResponse;
import com.roquito.web.response.ConfigResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by puran on 2/6/15.
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(method = RequestMethod.POST)
    public ApplicationResponse create(@RequestBody ApplicationDto applicationDto) {
        validateApiRequest(request, true);

        if (applicationDto == null || isEmpty(applicationDto.getUserId()) || isEmpty(applicationDto.getApplicationName())) {
            log.debug("Missing application data for creating new application");
            return new ApplicationResponse("Missing required data");
        }

        User owner = userService.findByUserId(applicationDto.getUserId());
        if (owner == null) {
            log.debug("Missing owner data, required for registering application");
            throw new RoquitoServerException("Owner required. Error creating new application");
        }
        Application savedApplication = applicationService.findByApplicationName(applicationDto.getApplicationName());
        if (savedApplication != null) {
            log.debug("Cannot register application. Application with same name already exists:" + applicationDto.getApplicationName());
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

        newApplication.setCreatedDate(new Date());

        String internalId = applicationService.saveApplication(newApplication);
        if (internalId != null) {
            log.info("Application registered with name:" + newApplication.getApplicationName());

            applicationDto.setApplicationId(applicationId);
            applicationDto.setApplicationKey(applicationKey);
            applicationDto.setApplicationSecret(applicationSecret);

            return new ApplicationResponse(applicationDto);
        }
        throw new RoquitoServerException("Error creating new application. Try again later.");
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ConfigResponse updateAppConfig(@RequestBody AppConfigDto configDto) {
        validateApiRequest(request, true);
        if (configDto == null || isEmpty(configDto.getType())) {
            log.debug("Missing application data for creating application config");
            return new ConfigResponse(null, true, "Missing required data");
        }
        Application savedApplication = applicationService.findByApplicationId(configDto.getApplicationId());
        if (savedApplication == null) {
            throw new RoquitoServerException("Wrong application identifier");
        }

        if (!DataConstants.TYPE_ANDROID.equals(configDto.getType()) &&
                !DataConstants.TYPE_IOS.equals(configDto.getType())) {
            return new ConfigResponse(null, true, "Wrong type defined");
        }

        Map<String, String> params = new HashMap<>();

        if (DataConstants.TYPE_ANDROID.equals(configDto.getType())) {
            params.put(DataConstants.ANDROID_GCM_KEY, configDto.getAndroidGCMKey());
        } else if (DataConstants.TYPE_IOS.equals(configDto.getType())) {
            params.put(DataConstants.DEV_APNS_CERT_FILE_PATH, configDto.getApnsDevCertFilePath());
            params.put(DataConstants.DEV_APNS_CERT_PASSWORD, configDto.getApnsDevCertPassword());
            params.put(DataConstants.PROD_APNS_CERT_FILE_PATH, configDto.getApnsProdCertFilePath());
            params.put(DataConstants.PROD_APNS_CERT_PASSWORD, configDto.getApnsDevCertPassword());
        }

        List<ApplicationConfig> appConfigs = savedApplication.getApplicationConfigs();
        ApplicationConfig applicationConfig = null;
        if (appConfigs == null) {
            appConfigs = new ArrayList<>();
            applicationConfig = new ApplicationConfig();
            applicationConfig.setType(configDto.getType());

            appConfigs.add(applicationConfig);
        } else {
            for (ApplicationConfig config : appConfigs) {
                if (config.getType().equals(configDto.getType())) {
                    applicationConfig = config;
                    break;
                }
            }
        }
        if (applicationConfig != null) {
            applicationConfig.setConfigParams(params);

            savedApplication.setApplicationConfigs(appConfigs);
            //update
            String internalId = applicationService.saveApplication(savedApplication);
            if (internalId != null) {
                log.info("Application config updated for application:" + savedApplication.getApplicationId());
                return new ConfigResponse("SUCCESS", false, null);
            }
        }
        throw new RoquitoServerException("Error updating application config. Try again later.");
    }

    @RequestMapping(value = "{applicationId}", method = RequestMethod.GET)
    public Application getUser(@PathVariable("applicationId") String applicationId) {
        Application savedApplication = applicationService.findByApplicationId(applicationId);

        return savedApplication;
    }
}

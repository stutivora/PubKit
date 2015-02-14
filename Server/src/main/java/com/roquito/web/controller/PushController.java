package com.roquito.web.controller;

import com.roquito.platform.notification.ApnsNotification;
import com.roquito.platform.notification.GcmNotification;
import com.roquito.platform.notification.PusherService;
import com.roquito.web.exception.RoquitoServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * This is a public API for sending push notification. This API doesn't return or
 * block the caller for the actual push response. It just puts the notification request
 * to the queue and returns.
 * <p/>
 * Created by puran on 2/7/15.
 */
@RestController
@RequestMapping("/push")
public class PushController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(PushController.class);

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "/gcm", method = RequestMethod.POST)
    public String create(@RequestBody GcmNotification gcmNotification) {
        if (gcmNotification == null) {
            log.debug("Null gcm notification data received");
            new RoquitoServerException("Invalid request");
        }
        String applicationId = gcmNotification.getApplicationId();
        validateApiRequest(request, applicationId);

        PusherService.getInstance().sendGcmPushNotification(gcmNotification);

        return "OK";
    }

    @RequestMapping(value = "/apns", method = RequestMethod.POST)
    public String create(@RequestBody ApnsNotification apnsNotification) {
        if (apnsNotification == null) {
            log.debug("Null apns notification data received");
            new RoquitoServerException("Invalid request");
        }
        String applicationId = apnsNotification.getApplicationId();
        validateApiRequest(request, applicationId);

        PusherService.getInstance().sendApnsPushNotification(apnsNotification);
        log.info("Added APNS notification message to the pusher queue");
        return "OK";
    }
}

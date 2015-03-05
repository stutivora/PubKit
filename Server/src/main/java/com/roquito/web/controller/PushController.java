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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.roquito.platform.notification.ApnsNotification;
import com.roquito.platform.notification.GcmNotification;
import com.roquito.platform.notification.PusherService;
import com.roquito.web.exception.RoquitoServerException;

/**
 * This is a public API for sending push notification. This API doesn't return
 * or block the caller for the actual push response. It just puts the
 * notification request to the queue and returns.
 * 
 * Created by puran
 */
@RestController
@RequestMapping("/push")
public class PushController extends BaseController {
    
    private static final Logger LOG = LoggerFactory.getLogger(PushController.class);

    @Autowired
    private PusherService pusherService;
    
    @RequestMapping(value = "/gcm", method = RequestMethod.POST)
    public String create(@RequestBody GcmNotification gcmNotification) {
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
    public String create(@RequestBody ApnsNotification apnsNotification) {
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
}

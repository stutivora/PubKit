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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.roquito.platform.commons.RoquitoKeyGenerator;
import com.roquito.platform.commons.RoquitoUtils;
import com.roquito.platform.model.Application;
import com.roquito.platform.service.ApplicationService;
import com.roquito.platform.service.SessionService;
import com.roquito.platform.service.UserService;
import com.roquito.web.exception.RoquitoAuthException;
import com.roquito.web.exception.RoquitoServerException;

/**
 * Created by puran
 */
public class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);
    
    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String API_KEY_PARAM = "api_key";
    
    protected final RoquitoKeyGenerator keyGenerator = new RoquitoKeyGenerator();
    
    @Autowired
    protected UserService userService;
    @Autowired
    protected ApplicationService applicationService;
    @Autowired
    protected SessionService sessionService;
    @Autowired
    protected HttpServletRequest httpRequest;
    @Autowired
    protected HttpServletResponse httpResponse;
    
    protected void validateAccessToken() {
        String accessToken = httpRequest.getParameter(ACCESS_TOKEN_PARAM);
        boolean tokenValid = sessionService.isAccessTokenValid(accessToken);
        if (!tokenValid) {
            log.debug("Request not authorized");
            throwAuthException();
        }
    }
    
    protected boolean validateApiRequest(String applicationId) {
        boolean validRequest = true;
        if (applicationId == null) {
            throwAuthException();
        }
        String apiKey = httpRequest.getHeader(API_KEY_PARAM);
        Application application = applicationService.findByApplicationId(applicationId);
        if (application == null) {
            throwAuthException();
        }
        if (application.getApplicationKey().equals(apiKey)) {
            validRequest = true;
        }
        if (!validRequest) {
            log.debug("Request not authorized");
            throwAuthException();
        }
        return validRequest;
    }
    
    protected void throwAuthException() {
        throw new RoquitoAuthException("Request not authorized");
    }
    
    protected void sendErrorResponse(HttpServletResponse httpServletResponse, int code, String message) {
        try {
            httpServletResponse.sendError(code, message);
        } catch (IOException e) {
            log.error("Error sending error response", e);
        }
    }
    
    protected boolean hasValue(String value) {
        return RoquitoUtils.hasValue(value);
    }
    
    protected boolean isEmpty(String value) {
        return !RoquitoUtils.hasValue(value);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
    
    @ExceptionHandler(RoquitoServerException.class)
    void handleServerError(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

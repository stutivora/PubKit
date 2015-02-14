package com.roquito.web.controller;

import com.roquito.platform.commons.RoquitoKeyGenerator;
import com.roquito.platform.commons.RoquitoUtils;
import com.roquito.platform.model.Application;
import com.roquito.platform.service.ApplicationService;
import com.roquito.platform.service.RedisService;
import com.roquito.platform.service.UserService;
import com.roquito.web.exception.RoquitoAuthException;
import com.roquito.web.exception.RoquitoServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by puran on 2/4/15.
 */
public class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String API_KEY_PARAM = "API_KEY";
    private static final String APP_ID_PARAM = "app_id";

    protected final UserService userService = new UserService();
    protected final RedisService redisService = new RedisService();
    protected final ApplicationService applicationService = new ApplicationService();
    protected final RoquitoKeyGenerator keyGenerator = new RoquitoKeyGenerator();

    protected boolean validateApiRequest(HttpServletRequest httpServletRequest, boolean needAccessToken) {
        String applicationId = httpServletRequest.getParameter(APP_ID_PARAM);
        return validateApiRequest(httpServletRequest, applicationId, needAccessToken);
    }

    protected boolean validateApiRequest(HttpServletRequest httpRequest, String applicationId, boolean needsAccessToken) {
        boolean validRequest = validateApiRequest(httpRequest, applicationId);
        if (needsAccessToken) {
            String accessToken = httpRequest.getParameter(ACCESS_TOKEN_PARAM);
            validRequest = redisService.isAccessTokenValid(accessToken);
        }
        if (!validRequest) {
            log.debug("Request not authorized");
            throwAuthException();
        }
        return validRequest;
    }

    protected boolean validateApiRequest(HttpServletRequest httpRequest, String applicationId) {
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

    private void throwAuthException() {
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

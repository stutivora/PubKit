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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pubkit.platform.commons.RoquitoUtils;
import com.pubkit.platform.model.Application;
import com.pubkit.platform.model.User;
import com.pubkit.web.data.ApplicationData;
import com.pubkit.web.data.UserData;
import com.pubkit.web.data.UserLoginData;
import com.pubkit.web.exception.RoquitoServerException;
import com.pubkit.web.response.ApplicationResponse;
import com.pubkit.web.response.LoginResponse;
import com.pubkit.web.response.UserResponse;

/**
 * Created by puran
 */
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    
    @RequestMapping(method = RequestMethod.POST)
    public UserResponse create(@RequestBody UserData user) {
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null
                || user.getPassword().isEmpty() || user.getFullName() == null || user.getFullName().isEmpty()) {
            LOG.debug("Missing required user information. Error creating new user account");
            return new UserResponse(null, true, "Missing required parameters");
        }
        User savedUser = userService.findByEmail(user.getEmail());
        if (savedUser == null) {
            User dbUser = new User();
            dbUser.setEmail(user.getEmail());
            String passwordHash = RoquitoUtils.getPasswordHash(user.getPassword());
            if (passwordHash != null) {
                dbUser.setPassword(passwordHash);
            } else {
                throw new RoquitoServerException("Unknown error. Please try again later.");
            }
            String userId = applicationService.getNextId(User.class);
            dbUser.setUserId(userId);
            dbUser.setFullName(user.getFullName());
            dbUser.setCompany(user.getCompany());
            dbUser.setProfilePicUrl(user.getProfilePicUrl());
            dbUser.setCreatedDate(new Date());
            
            String objectId = userService.saveUser(dbUser);
            if (objectId != null) {
                user.setUserId(userId);
                return new UserResponse(user);
            } else {
                LOG.debug("Error saving user data with response:" + objectId);
                throw new RoquitoServerException("Server error. Please try again later.");
            }
        } else {
            LOG.info("User already exists. Not creating new account");
            return new UserResponse(null, true, "User already exists");
        }
    }
    
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody UserLoginData userLoginData) {
        if (userLoginData == null || userLoginData.getEmail() == null || userLoginData.getEmail().isEmpty()
                || userLoginData.getPassword() == null || userLoginData.getPassword().isEmpty()) {
            return new LoginResponse("Missing required input data");
        }
        User user = userService.findByEmail(userLoginData.getEmail());
        if (user == null) {
            return new LoginResponse("User not recognized");
        } else {
            boolean matches = RoquitoUtils.comparePasswords(userLoginData.getPassword(), user.getPassword());
            if (matches) {
                String accessToken = keyGenerator.getSecureSessionId();
                boolean success = userService.saveAccessToken(userLoginData.getEmail(), accessToken);
                if (success) {
                    return new LoginResponse(user.getUserId(), user.getFullName(), accessToken);
                } else {
                    LOG.error("Error saving access token");
                    throw new RoquitoServerException("Unknown error. Try again later.");
                }
            } else {
                return new LoginResponse("Username and password doesn't match");
            }
        }
    }
    
    @RequestMapping(value = "{userId}/applications", method = RequestMethod.GET)
    public ApplicationResponse getApplications(@PathVariable("userId") String userId) {
        validateAccessToken();
        if (userId == null || userId.isEmpty()) {
            return new ApplicationResponse("Missing required input");
        }
        
        User user = userService.findByUserId(userId);
        if (user == null) {
            return new ApplicationResponse("Invalid user");
        }
        List<Application> applications = applicationService.getUserApplications(user);
        if (applications != null) {
            List<ApplicationData> appDataList = new ArrayList<>();
            for (Application application : applications) {
                ApplicationData appData = getApplicationData(application, false);
                appDataList.add(appData);
            }
            return new ApplicationResponse(appDataList);
        } else {
            return new ApplicationResponse(new ArrayList<ApplicationData>());
        }
    }
    
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public UserData getUser(@PathVariable("userId") String userId) {
        return null;
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "{userId}")
    public void delete(@PathVariable String userId) {
        
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "{userId}")
    public UserData update(@PathVariable String userId, @RequestBody UserData user) {
        return null;
    }
}

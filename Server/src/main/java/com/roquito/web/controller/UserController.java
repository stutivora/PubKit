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

import com.roquito.platform.commons.RoquitoUtils;
import com.roquito.platform.model.Application;
import com.roquito.platform.model.User;
import com.roquito.web.dto.ApplicationDto;
import com.roquito.web.dto.UserDto;
import com.roquito.web.dto.UserLoginDto;
import com.roquito.web.exception.RoquitoServerException;
import com.roquito.web.response.ApplicationResponse;
import com.roquito.web.response.LoginResponse;
import com.roquito.web.response.UserResponse;

/**
 * Created by puran
 */
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    
    @RequestMapping(method = RequestMethod.POST)
    public UserResponse create(@RequestBody UserDto user) {
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
            String userId = userService.getNextUserId();
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
    public LoginResponse login(@RequestBody UserLoginDto userLoginDto) {
        if (userLoginDto == null || userLoginDto.getEmail() == null || userLoginDto.getEmail().isEmpty()
                || userLoginDto.getPassword() == null || userLoginDto.getPassword().isEmpty()) {
            return new LoginResponse("Missing required input data");
        }
        User user = userService.findByEmail(userLoginDto.getEmail());
        if (user == null) {
            return new LoginResponse("User not recognized");
        } else {
            boolean matches = RoquitoUtils.comparePasswords(userLoginDto.getPassword(), user.getPassword());
            if (matches) {
                String accessToken = keyGenerator.getSecureSessionId();
                boolean success = sessionService.saveAccessToken(userLoginDto.getEmail(), accessToken);
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
            List<ApplicationDto> appDtos = new ArrayList<>();
            for (Application application : applications) {
                ApplicationDto appDto = getApplicationDto(application);
                appDtos.add(appDto);
            }
            return new ApplicationResponse(appDtos);
        } else {
            return new ApplicationResponse(new ArrayList<ApplicationDto>());
        }
    }
    
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public UserDto getUser(@PathVariable("userId") String userId) {
        return null;
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "{userId}")
    public void delete(@PathVariable String userId) {
        
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "{userId}")
    public UserDto update(@PathVariable String userId, @RequestBody UserDto user) {
        return null;
    }
}

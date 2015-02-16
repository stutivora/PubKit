package com.roquito.web.controller;

import com.roquito.platform.commons.RoquitoUtils;
import com.roquito.platform.messaging.persistence.MapDB;
import com.roquito.platform.model.User;
import com.roquito.web.dto.UserDto;
import com.roquito.web.dto.UserLoginDto;
import com.roquito.web.exception.RoquitoServerException;
import com.roquito.web.response.LoginResponse;
import com.roquito.web.response.UserResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

/**
 * Created by puran on 2/4/15.
 */
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping(method = RequestMethod.POST)
    public UserResponse create(@RequestBody UserDto user) {
	validateApiRequest(request, false);

	if (user == null || user.getEmail() == null || 
		user.getEmail().isEmpty() || user.getPassword() == null
		|| user.getPassword().isEmpty() || user.getFullName() == null || user.getFullName().isEmpty()) {
	    log.debug("Missing required user information. Error creating new user account");
	    return new UserResponse(null, true, "Missing required parameters");
	}
	User savedUser = userService.findByEmail(user.getEmail());
	if (savedUser == null) {
	    User dbUser = new User();
	    dbUser.setEmail(user.getEmail());
	    try {
		dbUser.setPassword(RoquitoUtils.getPasswordHash(user.getPassword()));
	    } catch (Exception es) {
		log.error("Error hashing password", es);
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
		log.debug("Error saving user data with response:" + objectId);
		throw new RoquitoServerException("Server error. Please try again later.");
	    }
	} else {
	    log.info("User already exists. Not creating new account");
	    return new UserResponse(null, true, "User already exists");
	}
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody UserLoginDto userLoginDto) {
	validateApiRequest(request, false);
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
		boolean success = MapDB.getInstance().saveAccessToken(userLoginDto.getEmail(), accessToken);
		if (success) {
		    return new LoginResponse(user.getUserId(), accessToken);
		} else {
		    log.error("Error saving access token");
		    throw new RoquitoServerException("Unknown error. Try again later.");
		}
	    }
	}
	sendErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "Wrong credentials");
	return null;
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

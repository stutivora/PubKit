package com.roquito.web.response;

import com.roquito.web.dto.UserDto;

/**
 * Created by puran on 2/6/15.
 */
public class UserResponse {
    private UserDto userDto;
    private boolean error;
    private String errorMessage;

    public UserResponse(UserDto userDto) {
        this(userDto, false, null);
    }

    public UserResponse(UserDto userDto, boolean error, String errorMessage) {
        this.userDto = userDto;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

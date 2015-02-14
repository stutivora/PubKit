package com.roquito.web.response;

/**
 * Created by puran on 2/6/15.
 */
public class LoginResponse {
    private String userId;
    private String accessToken;
    private boolean error;
    private String errorMessage;

    public LoginResponse(String userId, String accessToken) {
        this(userId, accessToken, false, null);
    }

    public LoginResponse(String errorMessage) {
        this(null, null, true, errorMessage);
    }

    public LoginResponse(String userId, String accessToken, boolean error, String errorMessage) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}

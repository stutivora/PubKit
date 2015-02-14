package com.roquito.web.response;

/**
 * Created by puran on 2/7/15.
 */
public class ConfigResponse {
    private String message;
    private boolean error;
    private String errorResponse;

    public ConfigResponse(String message, boolean error, String errorResponse) {
        this.message = message;
        this.error = error;
        this.errorResponse = errorResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(String errorResponse) {
        this.errorResponse = errorResponse;
    }
}

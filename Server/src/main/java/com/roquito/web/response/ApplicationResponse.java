package com.roquito.web.response;

import com.roquito.web.dto.ApplicationDto;

/**
 * Created by puran on 2/6/15.
 */
public class ApplicationResponse {
    private ApplicationDto application;
    private boolean error;
    private String errorMessage;

    public ApplicationResponse(ApplicationDto application) {
        this(application, false, null);
    }

    public ApplicationResponse(String errorMessage) {
        this(null, true, errorMessage);
    }

    public ApplicationResponse(ApplicationDto application, boolean error, String errorMessage) {
        this.application = application;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public ApplicationDto getApplication() {
        return application;
    }

    public void setApplication(ApplicationDto application) {
        this.application = application;
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

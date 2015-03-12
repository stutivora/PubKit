package com.roquito.web.response;

public class UploadResponse {
    
    private String uploadId;
    private boolean error;
    private String errorMessage;
    
    public UploadResponse(String errorMessage) {
        this(null, true, errorMessage);
    }
    
    public UploadResponse(String uploadId, boolean error, String errorMessage) {
        super();
        this.uploadId = uploadId;
        this.error = error;
        this.errorMessage = errorMessage;
    }
    
    public String getUploadId() {
        return uploadId;
    }
    
    public boolean isError() {
        return error;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}

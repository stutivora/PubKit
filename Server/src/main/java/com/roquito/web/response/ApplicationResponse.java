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
package com.roquito.web.response;

import java.util.List;

import com.roquito.web.data.ApplicationData;

/**
 * Created by puran
 */
public class ApplicationResponse {
    private ApplicationData applicationData;
    private List<ApplicationData> applicationDatas;
    private boolean error;
    private String errorMessage;
    
    public ApplicationResponse(ApplicationData applicationData) {
        this(applicationData, false, null);
    }
    
    public ApplicationResponse(String errorMessage) {
        this(null, true, errorMessage);
    }
    
    public ApplicationResponse(ApplicationData applicationData, boolean error, String errorMessage) {
        this.applicationData = applicationData;
        this.error = error;
        this.errorMessage = errorMessage;
    }
    
    public ApplicationResponse(List<ApplicationData> applicationDatas) {
        this.applicationDatas = applicationDatas;
    }
    
    public ApplicationData getApplication() {
        return applicationData;
    }
    
    public void setApplication(ApplicationData applicationData) {
        this.applicationData = applicationData;
    }
    
    public List<ApplicationData> getApplications() {
        return applicationDatas;
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

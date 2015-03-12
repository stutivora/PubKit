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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.roquito.web.response.UploadResponse;

/**
 * Created by puran
 */
@Controller
public class FileUploadController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);
    
    private static final String TYPE_CERT = "cert";
    
    @RequestMapping(value = "/upload_cert", method = RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
    
    @RequestMapping(value = "/upload_cert", method = RequestMethod.POST)
    public @ResponseBody UploadResponse handleFileUpload(@RequestParam("applicationId") String applicationId,
            @RequestParam("fileType") String fileType, @RequestParam("file") MultipartFile multipartFile) {
        
        LOG.debug("Upload request received for file size:" + multipartFile.getSize());
        validateAccessToken();
        
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = multipartFile.getOriginalFilename();
            try {
                if (TYPE_CERT.equalsIgnoreCase(fileType)) {
                    if (!".p12".endsWith(multipartFile.getOriginalFilename())
                            || !".cert".endsWith(multipartFile.getOriginalFilename())) {
                        LOG.debug("Invalid file upload type received");
                        
                        return new UploadResponse("Invalid file type");
                    }
                }
                byte[] fileData = multipartFile.getBytes();
                String uploadId = applicationService.saveFile(fileData, fileName);
                if (uploadId != null) {
                    return new UploadResponse(uploadId, false, null);
                }
                
                return new UploadResponse("Failed to upload " + fileName);
            } catch (Exception e) {
                LOG.error("Error uploading file", e);
                return new UploadResponse("Failed to upload " + fileName);
            }
        } else {
            LOG.error("Error uploading file");
            return new UploadResponse("Failed to upload");
        }
    }
    
}
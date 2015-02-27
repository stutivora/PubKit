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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by puran
 */
@Controller
public class FileUploadController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
	return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String fileName,
	    @RequestParam("file") MultipartFile multipartFile) {
	LOG.debug("Upload request received for file size:" + multipartFile.getSize());
	validateApiRequest(request, true);
	
	if (!multipartFile.isEmpty()) {
	    try {
		File inputFile = new File(multipartFile.getOriginalFilename());
		multipartFile.transferTo(inputFile);
		if (inputFile != null) {
		    if (!".p12".endsWith(multipartFile.getOriginalFilename()) || 
			    !".cert".endsWith(multipartFile.getOriginalFilename())) {
			LOG.debug("Invalid file upload type received");
			return "Invalid file type";
		    }
		    boolean result = applicationService.saveFile(inputFile, fileName);
		    if (result) {
			return "Successfully uploaded " + fileName + "!";
		    }
		}
		return "Failed to upload " + fileName + ". Internal server error";
	    } catch (Exception e) {
		return "Failed to upload " + fileName + " => " + e.getMessage();
	    }
	} else {
	    return "Failed to upload " + fileName + " because the file was empty.";
	}
    }

}
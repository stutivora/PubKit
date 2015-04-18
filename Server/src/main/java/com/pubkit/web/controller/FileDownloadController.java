package com.pubkit.web.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.gridfs.GridFSDBFile;

@RestController
public class FileDownloadController extends BaseController {
    
    @RequestMapping(value = "/files/cert/{certId}", method = RequestMethod.GET)
    @ResponseBody
    public InputStreamResource getFile(@PathVariable("certId") String certId) {
        GridFSDBFile savedFile = applicationService.getFile(certId);
        if (savedFile != null) {
            httpResponse.setContentType("application/force-download");
            httpResponse.addHeader("Content-Disposition", "attachment; filename='" + savedFile.getFilename() + "'");
            
            return new InputStreamResource(savedFile.getInputStream());
        }
        return null;
    }
}

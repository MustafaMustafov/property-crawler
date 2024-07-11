package com.property.crawler.imotbg;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImotBGController {

    @RequestMapping(value = "/uploadPdf", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }

        try {
            byte[] bytes = file.getBytes();
            return "File uploaded successfully: " + file.getOriginalFilename();
        } catch (Exception e) {
            return "Failed to upload file: " + file.getOriginalFilename() + " - " + e.getMessage();
        }
    }
}

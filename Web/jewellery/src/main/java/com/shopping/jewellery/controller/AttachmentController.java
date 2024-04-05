package com.shopping.jewellery.controller;

import org.springframework.core.io.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("api/attachment")
public class AttachmentController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            String fileName = System.currentTimeMillis() + ".jpg";
            File localFile = new File("attachments/" + fileName);
            FileUtils.writeByteArrayToFile(localFile, file.getBytes());
            return fileName;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("download/{name}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String name) {
        try {
            File localFile = new File("attachments/"+name);
            FileInputStream fileInputStream = new FileInputStream(localFile);
            InputStreamResource resource = new InputStreamResource(fileInputStream);
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+localFile.getName());
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");
            return ResponseEntity.ok().headers(header).contentLength(localFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } catch (Exception e) {
            return null;
        }
    }
}

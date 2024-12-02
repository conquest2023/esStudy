package es.board.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@RestController
@Log4j2
public class UploadController {

    @Value("%{file.dir}")
    private  String fileDir;


    @PostMapping("/upload")
    public  String saveFile(HttpServletRequest request) throws ServletException, IOException {


        Collection<Part> parts=request.getParts();

        for (Part part : parts) {

            log.info("name={}",part.getName());
            Collection<String> headNames=part.getHeaderNames();

            for (String headName : headNames) {
                log.info("header{}:{}",headName,part.getHeader(headName));
            }

            log.info("submittedFilename={}",part.getSubmittedFileName());
            log.info("size={}",part.getSize());

            InputStream inputStream=part.getInputStream();
           String body=  StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

           log.info("body={}",body);

           if (StringUtils.hasText(part.getSubmittedFileName())){
               String fullPath=fileDir+part.getSubmittedFileName();

               log.info(fullPath);
               part.write(fullPath);
           }

        }


        return "upload-form";
    }

//    @PostMapping("/hello")
//    public  String saveFile(@RequestParam String name, @RequestParam MultipartFile file ,HttpServletRequest request) throws IOException {
//
//        log.info("request={}",request);
//        log.info("itemName={}",name);
//        log.info("multipartFile={}",file);
//
//        if(!file.isEmpty()){
//            String fullPath=fileDir+file.getOriginalFilename();
//
//            log.info(fullPath);
//            file.transferTo(new File(fullPath));
//        }
//
//        return "/upload";
//    }


}

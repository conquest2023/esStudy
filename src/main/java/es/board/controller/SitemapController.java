package es.board.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SitemapController {

    @GetMapping(value = "/sitemaps.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Resource> getSitemap() {
        Resource sitemap = new ClassPathResource("static/sitemaps.xml");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(sitemap);
    }
    }
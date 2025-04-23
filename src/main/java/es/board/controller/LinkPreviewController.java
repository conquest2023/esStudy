package es.board.controller;

import es.board.service.OpenGraphParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LinkPreviewController {

    private  final OpenGraphParserService openGraphParserService;

    @GetMapping("/preview")
    public ResponseEntity<Map<String, String>> getPreview(@RequestParam String url) {
        Map<String, String> result = openGraphParserService.extractOGMeta(url);
        return ResponseEntity.ok(result);
    }
}
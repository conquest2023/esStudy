package es.board.controller;

import es.board.controller.record.NormalizeRequest;
import es.board.controller.record.NormalizedContent;
import es.board.domain.NormalizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
public class TestController {

    private final NormalizeService normalizeService;
    @PostMapping(value="/normal/test", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> test(@RequestParam String text, @RequestParam String title) {
        NormalizedContent normalize = normalizeService.normalize(text, title);
        return ResponseEntity.ok(Map.of("hi", normalize));
    }

}

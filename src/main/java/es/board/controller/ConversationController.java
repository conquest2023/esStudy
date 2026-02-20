package es.board.controller;

import es.board.controller.model.dto.english.AudioDto;
import es.board.domain.english.SentenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConversationController {

    private final SentenceService sentenceService;

    @GetMapping("/audio")
    public ResponseEntity<?> getRandomSentences(@RequestParam String level) {
        log.info(level);
        List<AudioDto> audioUrls = sentenceService.getAudioUrls(level);

        return ResponseEntity.ok(Map.of("ok",audioUrls));
    }
}

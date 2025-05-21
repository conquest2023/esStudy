package es.board.controller;

import es.board.config.slack.SlackNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SlackController {

    private final SlackNotifier slackNotifier;
    @GetMapping("/send")
    public String sendSlackMessage() {
        slackNotifier.sendMessage("Workly에서 새로운 피드가 작성되었습니다!");
        return "Slack 메시지 전송됨!";
    }
}

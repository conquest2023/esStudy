package es.board.config.slack;

import es.board.controller.model.res.SlackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SlackNotifier {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;

    public void sendMessage(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SlackMessage slackMessage = new SlackMessage(message);
        HttpEntity<SlackMessage> request = new HttpEntity<>(slackMessage, headers);

        restTemplate.postForEntity(webhookUrl, request, String.class);
    }
}
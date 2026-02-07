package es.board.domain.feed.event.reply;

import es.board.domain.feed.event.ReplyCreatedEvent;
import es.board.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointReplyEventListener {

    private final PointService pointService;

    @EventListener(ReplyCreatedEvent.class)
    public void handleReplyCreated(ReplyCreatedEvent event) {
        String userId = event.getUserId();
        pointService.grantActivityPoint(userId, "답글", 3, 10);
    }
}

package es.board.domain.event.reply;

import es.board.domain.event.CommentCreatedEvent;
import es.board.domain.event.ReplyCreatedEvent;
import es.board.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointReplyEventListener {

    private final PointService pointService;

    @EventListener(ReplyCreatedEvent.class)
    public void handleReplyCreated(ReplyCreatedEvent event) {

        // 1. 이벤트에서 필요한 데이터 추출
        String userId = event.getUserId();

        pointService.grantActivityPoint(userId, "답글", 3, 10);
    }
}

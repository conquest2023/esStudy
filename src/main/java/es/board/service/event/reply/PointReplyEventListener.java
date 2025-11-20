package es.board.service.event.reply;

import es.board.service.event.CommentCreatedEvent;
import es.board.service.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointReplyEventListener {

    private final PointService pointService;

    @EventListener
    public void handleReplyCreated(CommentCreatedEvent event) {

        // 1. 이벤트에서 필요한 데이터 추출
        String userId = event.getUserId();

        pointService.grantActivityPoint(userId, "답글", 3, 10);
    }
}

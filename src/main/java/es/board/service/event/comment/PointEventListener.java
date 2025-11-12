package es.board.service.event.comment;

import es.board.service.event.CommentCreatedEvent;
import es.board.service.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final PointService pointService;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {

        // 1. 이벤트에서 필요한 데이터 추출
        String userId = event.getUserId();

        pointService.grantActivityPoint(userId, "댓글", 3, 10);
    }
}

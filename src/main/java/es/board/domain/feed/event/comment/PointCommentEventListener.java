package es.board.domain.feed.event.comment;

import es.board.domain.feed.event.CommentCreatedEvent;
import es.board.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointCommentEventListener {

    private final PointService pointService;

    @EventListener(CommentCreatedEvent.class)
    public void handleCommentCreated(CommentCreatedEvent event) {
        String userId = event.getUserId();

        pointService.grantActivityPoint(userId, "댓글", 3, 10);
    }
}

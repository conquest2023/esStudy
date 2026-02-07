package es.board.domain.feed.event.post;

import es.board.domain.feed.event.PostCreatedEvent;
import es.board.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPostEventListener {

    private final PointService pointService;


    @EventListener(PostCreatedEvent.class)
    public void handlePostCreated(PostCreatedEvent event) {

        String userId = event.getUserId();

        pointService.grantActivityPoint(userId, "피드", 5, 10);
    }
}

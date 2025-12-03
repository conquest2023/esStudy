package es.board.domain.event.poll;

import es.board.domain.event.PollCreatedEvent;
import es.board.domain.event.PostCreatedEvent;
import es.board.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPollEventListener {

    private final PointService pointService;


    @EventListener(PollCreatedEvent.class)
    public void handlePollCreated(PollCreatedEvent event) {

        String userId = event.getUserId();

        pointService.grantActivityPoint(userId, "피드", 10, 10);
    }
}

package es.board.domain.feed.event.poll;

import es.board.domain.PostRepository;
import es.board.domain.feed.event.PollCreatedEvent;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PollEventListener {

    private final NotificationService notificationService;
    private final PostRepository postRepository;

    @EventListener(PollCreatedEvent.class)
    public void handlePollCreated(PollCreatedEvent event) {

        Optional<PostEntity> entity = postRepository.findById(event.getPostId());

        String postOwnerId = entity.get().getUserId();
        String username = entity.get().getUsername();
        if (postOwnerId != null && postOwnerId.equals(event.getUserId())) {
            notificationService.sendNoticeNotification(
                    event.getPostId(),
                    username + "님이 투표를 게시했습니다: " + event.getRequest().getTitle()
            );

        }
    }
}
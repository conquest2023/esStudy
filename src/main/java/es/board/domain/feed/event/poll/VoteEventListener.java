package es.board.domain.feed.event.poll;

import es.board.domain.feed.event.VoteCreatedEvent;
import es.board.domain.notification.feed.FeedNotificationService;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.poll.PollRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.domain.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoteEventListener {

    private final FeedNotificationService notificationService;

    private final UserRepository userRepository;

    private final PollRepository pollRepository;
    @EventListener(VoteCreatedEvent.class)
    public void handleVoteCreated(VoteCreatedEvent event) {

        Optional<PostEntity> entity = pollRepository.findByPost(event.getRequest().getPollId());
        Optional<User> user = userRepository.findByUserId(event.getUserId());
        String postOwnerId = entity.get().getUserId();
        String username = user.get().getUsername();
        if (postOwnerId != null && !postOwnerId.equals(event.getRequest().getVoterId())) {
            notificationService.sendCommentNotification(
                    postOwnerId,
                    entity.get().getId(),
                    username + "님이 투표를 했습니다:"+entity.get().getTitle());
        }
    }
}

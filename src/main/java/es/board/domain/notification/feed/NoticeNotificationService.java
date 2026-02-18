package es.board.domain.notification.feed;

import es.board.domain.notification.NotificationService;
import es.board.domain.notification.NotificationType;
import es.board.repository.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeNotificationService {

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public void sendNoticeNotification(int postId, String message) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        List<String> userIds = userRepository.findMonthActiveUser(lastMonth);
        for (String userId : userIds) {
             notificationService.sendFeedEvent(userId, postId, NotificationType.NOTICE, "notice-notification", message);
        }
    }
}

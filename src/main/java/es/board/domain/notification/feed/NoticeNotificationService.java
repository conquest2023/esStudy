package es.board.domain.notification.feed;

import es.board.domain.notification.NotificationService;
import es.board.domain.notification.NotificationType;
import es.board.repository.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeNotificationService {

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public void sendNoticeNotification(int postId, String message) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        List<String> userIds = userRepository.findMonthActiveUser(lastMonth);
        Map<String, Object> payload = new HashMap<>();
        payload.put("message",message);
        payload.put("postId", postId);
        for (String userId : userIds) {
             notificationService.sendEvent(userId, payload, NotificationType.NOTICE);
        }
    }
}

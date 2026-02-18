package es.board.domain.notification.feed;


import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.record.MissingPollPayload;
import es.board.domain.notification.NotificationService;
import es.board.domain.notification.NotificationType;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PollNotificationService {


    private final NotificationService notificationService;

    private  final ObjectMapper objectMapper;


//    public void sendTop1RankingNotification(String userId, PostEntity top1){
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime lastMonth = now.minusMonths(1);
//        List<String> userIds = userRepository.findMonthActiveUser(lastMonth);
//        for (String id : userIds) {
//           notificationService.sendFeedNotification(id, top1.getId(), RANK_TOP1_EVENT,
//                    "rank-top1-notification",
//                    "랭킹 1위님이 글을 작성하셨습니다: "+ top1.getTitle());
//        }
//    }

    public void sendMissingPollNotification(String userId, MissingPollPayload pollPayload) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "notifications:poll");
            payload.put("count", pollPayload.count());
            List<Map<String, Object>> posts = pollPayload.items().stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("postId", p.postId());
                map.put("title", p.title());
                return map;
            }).toList();
            payload.put("posts", posts);
            payload.put("message", "아직 투표 안한게" + pollPayload.count() +" 개 있습니다.");
            String json = objectMapper.writeValueAsString(payload);
            notificationService.sendPollEvent(userId, json, NotificationType.POLL);
        } catch (Exception e) {
            log.error("미 투표 알림 실패", e);
        }
    }

}

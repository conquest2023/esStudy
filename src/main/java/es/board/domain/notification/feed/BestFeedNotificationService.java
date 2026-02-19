package es.board.domain.notification.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.domain.notification.NotificationService;
import es.board.domain.notification.NotificationType;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.board.domain.notification.NotificationType.RANK_TOP1;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestFeedNotificationService {



    private final NotificationService notificationService;


    private final UserRepository userRepository;


    public void sendTop3BestFeedNotification(String userId, List<PostEntity> top3){
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "rank-top3-hourly");
            payload.put("count", top3.size());
            List<Map<String, Object>> posts = top3.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("postId", p.getId());
                map.put("title", p.getTitle());
                map.put("username", p.getUsername());
                return map;
            }).toList();
            payload.put("posts", posts);
            payload.put("message", "오늘의 베스트 게시글 Top 3가 업데이트되었습니다!");
            notificationService.sendEvent(userId, payload, NotificationType.RANK_TOP3);
        } catch (Exception e) {
            log.error("TOP3 랭킹 알림 실패", e);
        }
    }

    public void sendAnalysisNotification(String userId, List<String> message) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "analysis-user");
            payload.put("analysis", message);
            payload.put("message", userId + "님의 하루 분석 결과입니다.");

           notificationService.sendEvent(userId, payload, NotificationType.ANALYSIS);
        } catch (Exception e) {
            log.error("분석 알림 실패", e);
        }
    }

    public void sendTop1UserFeedNotification(PostEntity top1){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        List<String> userIds = userRepository.findMonthActiveUser(lastMonth);
        Map<String, Object> payload = new HashMap<>();
        payload.put("message","랭킹 1위님이 글을 작성하셨습니다: "+ top1.getTitle());
        payload.put("postId", top1.getId());
        for (String id : userIds) {
           notificationService.sendEvent(id, payload, RANK_TOP1);
        }
    }
}

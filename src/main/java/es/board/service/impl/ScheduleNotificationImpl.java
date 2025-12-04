package es.board.service.impl;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.NotificationService;
import es.board.service.ScheduleNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleNotificationImpl implements ScheduleNotificationService {

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final PostQueryRepository postQueryRepository;
    @Override
    @Scheduled(cron = "5 0 * * * *", zone = "Asia/Seoul")
    public void sendTop3Hourly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.minusDays(1);
        List<PostEntity> todayTop3 = postQueryRepository.findTodayTop3(lastDay);
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        List<String> userIds = userRepository.findMonthActiveUser(oneMonthAgo);
        for (String userId : userIds) {
            notificationService.sendTop3RankingNotification(userId, todayTop3);
        }
    }
    @Override
    @Scheduled(cron = "5 0 * * * *", zone = "Asia/Seoul")
    public void sendRank1stEvery2h() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.minusDays(1);
        Optional<PostEntity> userTopToday = postQueryRepository.findUserTopToday(lastDay);
        userTopToday.ifPresent(postEntity ->
                notificationService.sendTop1RankingNotification(postEntity.getUserId(), postEntity));

    }

    @Override
    public void flushQuietHoursQueue() {

    }
}

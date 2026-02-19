package es.board.domain.notification;

import es.board.infrastructure.english.entity.EnglishProblemAttempt;
import es.board.infrastructure.english.entity.EnglishProblemAttemptRepository;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EnglishNotificationService {

    private final NotificationService notificationService;

    private final EnglishProblemAttemptRepository englishProblemAttemptRepository;

    private final UserRepository userRepository;

    public void sendEnglishPracticeNotification(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        List<String> userIds = userRepository.findMonthActiveUser(lastMonth);
        List<EnglishProblemAttempt> byUserLog = englishProblemAttemptRepository.findByUserLog(userIds);
        calculateStreak(byUserLog);
    }

    public void calculateStreak(List<EnglishProblemAttempt> list){

        Map<String, Set<LocalDate>> userAttempts = list.stream()
                .filter(o -> o.getUserId() != null)
                .collect(Collectors.groupingBy(
                        EnglishProblemAttempt::getUserId,
                        Collectors.mapping(
                                attempt -> attempt.getCreatedAt().toLocalDate(),
                                Collectors.toSet()
                        )
                ));
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        userAttempts.forEach((userId, dateSet) -> {
            // 1. 어제 기록이 있는지 확인
            boolean playedYesterday = dateSet.contains(yesterday);
            // 2. 오늘 기록이 아직 없는지 확인
            boolean playedToday = dateSet.contains(today);

            if (playedYesterday && !playedToday) {
                int currentStreak = calculateStreakCount(dateSet);

                // 알림 발송
                notificationService.sendEnglishEvent(userId, NotificationType.ENGLISH_PRACTICE,
                        "🔥 연속 " + currentStreak + "일 기록이 깨지기 직전이에요! 오늘 영어 학습을 완료하세요.");
            } else {
                // [CASE B] 기록이 아예 없는 사람 (이번 달 활동은 있지만 문제는 안 푼 사람)
                notificationService.sendEnglishEvent(userId, NotificationType.ENGLISH_PRACTICE,
                        "👋 영어가 망설여지시나요? 오늘 딱 3문제만 가볍게 시작해보세요!");
            }
        });
    }
    public int calculateStreakCount(Set<LocalDate> dates) {
        if (dates.isEmpty())
            return 0;

        List<LocalDate> sortedDates = dates.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate target = sortedDates.get(0);

        // 마지막 기록이 오늘이나 어제가 아니면 이미 끊긴 것
        if (!target.equals(today) && !target.equals(today.minusDays(1))) {
            return 0;
        }
        // 연속성 체크
        for (int i = 0; i < sortedDates.size() - 1; i++) {
            streak++;
            // 현재 날짜와 다음 날짜의 차이가 1일이 아니면 중단
            if (!sortedDates.get(i).minusDays(1).equals(sortedDates.get(i + 1))) {
                break;
            }
        }
        return streak;
    }

}

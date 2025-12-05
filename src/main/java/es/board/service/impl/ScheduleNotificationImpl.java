package es.board.service.impl;

import es.board.controller.record.MissingPollItem;
import es.board.controller.record.MissingPollPayload;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.poll.PollEntity;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.infrastructure.poll.PollRepository;
import es.board.infrastructure.poll.PollVoteRepository;
import es.board.infrastructure.projection.PollAnswerRow;
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
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleNotificationImpl implements ScheduleNotificationService {

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final PostQueryRepository postQueryRepository;

    private final PollRepository pollRepository;

    private final PollVoteRepository pollVoteRepository;
    @Override
    @Scheduled(cron = "0 0 0/2 * * *", zone = "Asia/Seoul")
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
    @Scheduled(cron = "0 0 0/3 * * *", zone = "Asia/Seoul")
    public void sendRank1stEvery2h() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.minusDays(1);
        Optional<PostEntity> userTopToday = postQueryRepository.findUserTopToday(lastDay);
        userTopToday.ifPresent(postEntity ->
                notificationService.sendTop1RankingNotification(postEntity.getUserId(), postEntity));

    }

    @Override
    @Scheduled(cron = "0 0 0/2 * * *", zone = "Asia/Seoul")
    public void sendPoll() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since3Days = now.minusDays(3);

        List<PollEntity> polls = pollRepository.findBy3DaysAgoPoll(since3Days);
        if (polls.isEmpty())
            return;

        // pollId 목록 + pollId->postId 맵 + postId 목록
        List<Long> pollIds = new ArrayList<>(polls.size());
        Map<Long, Integer> pollIdToPostId = new HashMap<>(polls.size());
        Set<Integer> postIds = new HashSet<>(polls.size());

        for (PollEntity p : polls) {
            Long pollId = p.getId();
            if (p.getPost() == null || p.getPost().getId() == 0)
                continue;
            Integer postId = p.getPost().getId();
            pollIds.add(pollId);
            pollIdToPostId.put(pollId, postId);
            postIds.add(postId);
        }
        if (pollIds.isEmpty())
            return;

        // 2) Post 한 번에 로딩하여 postId -> title 맵 구성
        List<PostEntity> posts = postQueryRepository.findPostAndPollEntity(new ArrayList<>(postIds));
        Map<Integer, String> postIdToTitle = posts.stream()
                .collect(Collectors.toMap(PostEntity::getId, PostEntity::getTitle));

        List<String> userIds = userRepository.findMonthActiveUser(now.minusDays(30));
        if (userIds == null || userIds.isEmpty())
            return;

        List<PollAnswerRow> rows = pollVoteRepository.findAllByPollIdIn(pollIds);
        Map<String, Set<Long>> participatedByUser = new HashMap<>();
        for (PollAnswerRow r : rows) {
            participatedByUser
                    .computeIfAbsent(r.getVoterId(), k -> new HashSet<>())
                    .add(r.getPollId());
        }

        // 5) 유저별 미참여 poll 계산 → (pollId, postId, title)로 패킹
        for (String userId : userIds) {
            Set<Long> participated = participatedByUser.getOrDefault(userId, Collections.emptySet());

            List<Long> missingPollIds = pollIds.stream()
                    .filter(id -> !participated.contains(id))
                    .toList();

            if (missingPollIds.isEmpty())
                continue;

            //여기서 pollId -> postId -> title 순으로 조회해야 함!
            List<MissingPollItem> items = missingPollIds.stream()
                    .map(pollId -> {
                        Integer postId = pollIdToPostId.get(pollId);
                        if (postId == null) return null;
                        String title = postIdToTitle.getOrDefault(postId, "(제목 없음)");

                        return new MissingPollItem(postId, title);
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (items.isEmpty())
                continue;

            MissingPollPayload payload = new MissingPollPayload(items.size(), items);

            notificationService.sendMissingPollNotification(userId, payload); // 실제 발송
        }
    }
}

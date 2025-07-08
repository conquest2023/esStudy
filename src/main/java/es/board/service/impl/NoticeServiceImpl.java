package es.board.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.repository.entity.Notice;
import es.board.repository.entity.repository.NoticeRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.NoticeService;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl  implements NoticeService {



    private final AsyncService asyncService;

    private  final NotificationService notificationService;

    private final UserRepository userRepository;

    private final NoticeRepository noticeRepository;



    private final StringRedisTemplate redisTemplate;


    private final FeedMapper feedMapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;


    private static final String NOTICE_KEY = "notice_Daily";



    @Override
    public List<NoticeDTO.Request> getAllNotices() {
        String cachedNotices = redisTemplate.opsForValue().get(NOTICE_KEY);

        if (cachedNotices != null) {
            log.info("캐시 성공!");
            return deserializeNotices(cachedNotices);
        }
        List<NoticeDTO.Request> notices = feedMapper.fromNoticeList(noticeRepository.findNoticeByCreatedAtDESC());
        redisTemplate.opsForValue().set(NOTICE_KEY, serializeNotices(notices), Duration.ofHours(1));
        return notices;
    }


    @Override
    public NoticeDTO.Request getOneNotice(Long id) {

        return feedMapper.fromNotice(noticeRepository.findByNoticeOne(id));
    }

    @Override
    public void createNotice(String token, NoticeDTO.Request noticeDTO) {
        String feedUID= java.util.UUID.randomUUID().toString();
        String userId = jwtTokenProvider.getUserId(token);
        if (!isAdmin(userId)) {
            throw new RuntimeException("관리자만 공지사항을 등록할 수 있습니다!");
        }
        NoticeDTO.Request notice=feedMapper.fromNoticeDocument(NoticeSaveId(noticeDTO, token,feedUID), jwtTokenProvider.getUserId(token),feedUID);
        CompletableFuture.supplyAsync(() -> {
            asyncService.saveNoticeAsync(notice,notice.getId());
            return null;
        });
        redisTemplate.delete(NOTICE_KEY);
        List<String> userIds = userRepository.findAllUserIds();
        notificationService.sendNoticeNotification(userIds,String.valueOf(notice.getFeedUID()), "📢 새로운 공지사항이 등록되었습니다!");
    }

    private boolean isAdmin(String userId) {

        return noticeRepository.existsByUserId(userId);

    }


    private Notice NoticeSaveId(NoticeDTO.Request noticeDTO, String token, String feedUID) {
       return noticeRepository.save(feedMapper.toNotice(noticeDTO, jwtTokenProvider.getUserId(token),feedUID));
    }
    private String serializeNotices(List<NoticeDTO.Request> notices) {
        try {
            return objectMapper.writeValueAsString(notices);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 직렬화 실패", e);
        }
    }

    private List<NoticeDTO.Request> deserializeNotices(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<NoticeDTO.Request>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 역직렬화 실패", e);
        }
    }
}
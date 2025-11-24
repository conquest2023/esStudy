package es.board.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.repository.entity.repository.NoticeJpaRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.NoticeService;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl  implements NoticeService {




    private  final NotificationService notificationService;

    private final UserRepository userRepository;

    private final NoticeJpaRepository noticeRepository;

    private final StringRedisTemplate redisTemplate;

    private final FeedMapper feedMapper;


    private final ObjectMapper objectMapper;


    private static final String NOTICE_KEY = "notice_Daily";



    @Override
    public NoticeDTO.Request getLatestNotice() {
        String cachedNotices = redisTemplate.opsForValue().get(NOTICE_KEY);

        if (cachedNotices != null) {
            log.info("캐시 성공!");
            return deserializeNotices(cachedNotices);
        }
        NoticeDTO.Request   notices = feedMapper.fromNotice(noticeRepository.findNoticeByCreatedAtDESC());
        redisTemplate.opsForValue().set(NOTICE_KEY, serializeNotices(notices), Duration.ofHours(1));
        return notices;
    }


    @Override
    public NoticeDTO.Request getDetailNotice(Long id) {

        return feedMapper.fromNotice(noticeRepository.findDetailNotice(id));
    }

    @Override
    public void createNotice(String userId, NoticeDTO.Request noticeDTO) {
//        String feedUID= java.util.UUID.randomUUID().toString();
        if (!isAdmin(userId)) {
            throw new RuntimeException("관리자만 공지사항을 등록할 수 있습니다!");
        }
//        NoticeDTO.Response notice=feedMapper.fromNoticeDocument(NoticeSaveId(noticeDTO, token,feedUID),feedUID);
//        CompletableFuture.supplyAsync(() -> {
//            asyncService.saveNoticeAsync(notice,notice.getId());
//            return null;
//        });
//        redisTemplate.delete(NOTICE_KEY);
        List<String> userIds = userRepository.findAllUserIds();
        notificationService.sendNoticeNotification(userIds,Math.toIntExact(noticeDTO.getId()), "📢 새로운 공지사항이 등록되었습니다!");
    }

    private boolean isAdmin(String userId) {

        return noticeRepository.existsByUserId(userId);

    }


//    private NoticeEntity NoticeSaveId(NoticeDTO.Response noticeDTO, String token, String feedUID) {
//       return noticeRepository.save(feedMapper.toNotice(noticeDTO, jwtTokenProvider.getUserId(token),feedUID));
//    }
    
    private String serializeNotices(NoticeDTO.Request notices) {
        try {
            return objectMapper.writeValueAsString(notices);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 직렬화 실패", e);
        }
    }

    private NoticeDTO.Request deserializeNotices(String json) {
        try {
            return objectMapper.readValue(json,NoticeDTO.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 역직렬화 실패", e);
        }
    }
}
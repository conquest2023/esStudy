package es.board.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.entity.Notice;
import es.board.repository.entity.entityrepository.NoticeRepository;
import es.board.repository.entity.entityrepository.PostRepository;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl  implements NoticeService {

    private final FeedMapper feedMapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final AsyncService asyncService;

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private static final String NOTICE_KEY = "notice_daily";


    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDTO> getAllNotices() {
        String cachedNotices = redisTemplate.opsForValue().get(NOTICE_KEY);

//        if (cachedNotices != null) {
//            return deserializeNotices(cachedNotices);
//        }
        List<NoticeDTO> notices = feedMapper.fromNoticeList(noticeRepository.findNoticeByCreatedAtDESC());
//      redisTemplate.opsForValue().set(NOTICE_KEY, serializeNotices(notices), Duration.ofHours(1));
        return notices;
    }


    @Override
    public NoticeDTO getOneNotice(Long id) {
        return feedMapper.fromNotice(noticeRepository.findByNoticeOne(id));
    }

    @Override
    public void createNotice(String token, NoticeDTO noticeDTO) {

        String userId = jwtTokenProvider.getUserId(token);
        if (!isAdmin(userId)) {
            throw new RuntimeException("관리자만 공지사항을 등록할 수 있습니다!");
        }
        CompletableFuture.supplyAsync(() -> {
            NoticeDTO notice=feedMapper.ToNoticeDocument(NoticeSaveId(noticeDTO, token), jwtTokenProvider.getUserId(token));
            asyncService.saveNoticeAsync(notice,notice.getId());
            return null;
        });
    }
    private boolean isAdmin(String userId) {
        return noticeRepository.existsByUserId(userId);
    }
    private Notice NoticeSaveId(NoticeDTO noticeDTO, String token) {
       return noticeRepository.save(feedMapper.ToNotice(noticeDTO, jwtTokenProvider.getUserId(token)));
    }
    private String serializeNotices(List<NoticeDTO> notices) {
        try {
            return objectMapper.writeValueAsString(notices);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 직렬화 실패", e);
        }
    }

    private List<NoticeDTO> deserializeNotices(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<NoticeDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 역직렬화 실패", e);
        }
    }
}
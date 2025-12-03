package es.board.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.controller.model.mapper.PostDomainMapper;
import es.board.infrastructure.entity.feed.NoticeEntity;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.repository.NoticeJpaRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.NoticeService;
import es.board.service.NotificationService;
import es.board.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl  implements NoticeService {

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;
    private static final String NOTICE_KEY = "notice";

    private  final NotificationService notificationService;

    private final UserRepository userRepository;

    private final NoticeJpaRepository noticeRepository;



    @Override
    public PostDTO.Response getLatestNotice() {
        String cachedNotices = redisTemplate.opsForValue().get(NOTICE_KEY);
        if (cachedNotices != null) {
            return deserializeNotices(cachedNotices);
        }
        Post post = Post.toDomain(noticeRepository.findNoticeByCreatedAtDESC());
        PostDTO.Response response = PostDomainMapper.toResponse(post.getUserId(), post);
        redisTemplate.opsForValue().set(NOTICE_KEY, serializeNotices(response), Duration.ofHours(1));
        return response;
    }


    @Override
    public PostDTO.Response getDetailNotice(Long id) {
        PostEntity postEntity = noticeRepository.findDetailNotice(id);
        Post post = Post.toDomain(postEntity);
        return PostDomainMapper.toResponse(post.getUserId(),post);
    }

    @Override
    public Page<PostEntity> getNoticeList(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostEntity> byPagePosts = noticeRepository.findByPageNotices(category,pageable);

        return byPagePosts;
    }

    @Override
    @Transactional
    public void createNotice(String userId, NoticeDTO.Request noticeDTO) {

        if (!isAdmin(userId)) {
            throw new RuntimeException("관리자만 공지사항을 등록할 수 있습니다!");
        }

        redisTemplate.delete(NOTICE_KEY);
        Post post = PostDomainMapper.toDomain(
                userId,
                new PostDTO.Request("관리자",
                        noticeDTO.getTitle(),
                        noticeDTO.getDescription(),
                        "공지사항"));

        PostEntity entity = Post.toEntity(post);

        NoticeEntity notice = NoticeEntity.builder()
                .post(entity) // 세팅
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
        createNotification(notice);
    }


    private void createNotification(NoticeEntity notice) {
        notificationService.sendNoticeNotification(Math.toIntExact(notice.getPost().getId()), "새로운 공지사항이 등록되었습니다!");
    }

    private boolean isAdmin(String userId) {

        return noticeRepository.existsByUserId(userId);

    }


//    private NoticeEntity NoticeSaveId(NoticeDTO.Response noticeDTO, String token, String feedUID) {
//       return noticeRepository.save(feedMapper.toNotice(noticeDTO, jwtTokenProvider.getUserId(token),feedUID));
//    }
    
    private String serializeNotices(PostDTO.Response notices) {
        try {
            return objectMapper.writeValueAsString(notices);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 직렬화 실패", e);
        }
    }

    private PostDTO.Response deserializeNotices(String json) {
        try {
            return objectMapper.readValue(json, PostDTO.Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("공지사항 역직렬화 실패", e);
        }
    }
}
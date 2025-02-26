package es.board.service.impl;


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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl  implements NoticeService {

    private  final FeedMapper feedMapper;

    private  final PostRepository postRepository;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final AsyncService asyncService;

    private  final NoticeRepository noticeRepository;
    @Override
    public List<NoticeDTO> getAllNotices() {
        return feedMapper.fromNoticeList(noticeRepository.findAll());
    }

    @Override
    public NoticeDTO getOneNotice(Long id) {
        return  feedMapper.fromNotice(noticeRepository.findByNoticeOne(id));
    }

    @Override
    public void createNotice(String token, NoticeDTO noticeDTO) {

        String userId = jwtTokenProvider.getUserId(token);
        if (!isAdmin(userId)) {
            throw new RuntimeException("🚨 관리자만 공지사항을 등록할 수 있습니다!");
        }
            CompletableFuture.supplyAsync(() -> {
            Long savedNoticeId = NoticeSaveId(noticeDTO,token);
            asyncService.saveNoticeAsync(noticeDTO,savedNoticeId);
            return null;
        });
    }

    private  boolean isAdmin(String userId) {
        String  admin= noticeRepository.findByUserId(userId);


        return  admin!=null && Objects.equals(userId, admin);
    }

    private Long NoticeSaveId(NoticeDTO  noticeDTO, String token) {

        noticeDTO.setFeedUID(UUID.randomUUID().toString());
        Notice notice =   noticeRepository.save(feedMapper.ToNotice(noticeDTO,jwtTokenProvider.getUserId(token)));
        return notice.getId();
    }

    public void esSettingId(NoticeDTO noticeDTO, Long id) {

        log.info(String.valueOf(id));

        }
    }

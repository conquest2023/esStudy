package es.board.service.impl;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.NoticeDTO;
import es.board.repository.entity.entityrepository.NoticeRepository;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl  implements NoticeService {

    private  final FeedMapper feedMapper;

    private  final JwtTokenProvider jwtTokenProvider;

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
        noticeRepository.save(feedMapper.ToNotice(noticeDTO,jwtTokenProvider.getUserId(token)));
    }

    private  boolean isAdmin(String userId) {
        String  admin= noticeRepository.findByUserId(userId);


        return  admin!=null && Objects.equals(userId, admin);
    }
}

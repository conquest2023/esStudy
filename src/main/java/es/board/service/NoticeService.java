package es.board.service;

import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.infrastructure.entity.feed.PostEntity;
import org.springframework.data.domain.Page;

public interface NoticeService {

    PostDTO.Response getLatestNotice();

    PostDTO.Response getDetailNotice(Long id);


    Page<PostEntity> getNoticeList(int page , int size, String category);

    void createNotice(String userId, NoticeDTO.Request noticeDTO);
}

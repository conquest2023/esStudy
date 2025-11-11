package es.board.service;

import es.board.controller.model.dto.feed.NoticeDTO;

public interface NoticeService {

    NoticeDTO.Request getLatestNotice();

    NoticeDTO.Request getDetailNotice(Long id);


    void createNotice(String userId, NoticeDTO.Request noticeDTO);
}

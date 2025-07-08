package es.board.service;

import es.board.controller.model.dto.feed.NoticeDTO;

import java.util.List;

public interface NoticeService {

    List<NoticeDTO.Request> getAllNotices();

    NoticeDTO.Request getOneNotice(Long id);


    void createNotice(String token, NoticeDTO.Request noticeDTO);
}

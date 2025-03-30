package es.board.service;

import es.board.controller.model.req.NoticeRequest;

import java.util.List;

public interface NoticeService {

    List<NoticeRequest> getAllNotices();

    NoticeRequest getOneNotice(Long id);


    void createNotice(String token, NoticeRequest noticeDTO);
}

package es.board.service;

import es.board.controller.model.req.NoticeDTO;

import java.util.List;

public interface NoticeService {

    List<NoticeDTO> getAllNotices();

    NoticeDTO getOneNotice(Long id);


    void createNotice(String token, NoticeDTO noticeDTO);
}

package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.NoticeDTO;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final JwtTokenProvider jwtTokenProvider;

    private final NoticeService noticeService;

    // 📌 공지사항 목록 조회
    @GetMapping("/list/noitce")
    public ResponseEntity<List<NoticeDTO>> getNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }


    @GetMapping("/list/one/notice/{id}")
    public ResponseEntity<NoticeDTO> getNoticeOne(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getOneNotice(id));
    }


    // 📌 공지사항 등록 (관리자만 가능)
    @PostMapping("/add/notice")
    public void createNotice(@RequestHeader(value = "Authorization") String token, @RequestBody NoticeDTO noticeDTO) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                noticeService.createNotice(token,noticeDTO);
            }
        }
    }
}

package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.config.s3.S3Uploader;
import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NoticeController {

    private final JwtTokenProvider jwtTokenProvider;

    private final NoticeService noticeService;

    private final S3Uploader s3Uploader;


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
    public void createNotice(@RequestHeader(value = "Authorization") String token,
                             @ModelAttribute NoticeDTO noticeDTO,
                             @RequestParam(required = false, value = "imageFile") MultipartFile file) throws IOException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId=jwtTokenProvider.getUserId(token);
                processFileUpload(file, noticeDTO,userId);
                noticeService.createNotice(token,noticeDTO);
            }
        }
    }

    private void processFileUpload(MultipartFile file, NoticeDTO noticeDTO, String  userId) throws IOException {
        if (file != null && !file.isEmpty()) {
            log.info("File upload started");
            noticeDTO.setImageURL(s3Uploader.upload(file,userId));
        }
    }
}

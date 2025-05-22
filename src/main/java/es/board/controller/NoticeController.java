package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.config.s3.S3Uploader;
import es.board.controller.model.req.NoticeRequest;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")

public class NoticeController {

    private final JwtTokenProvider jwtTokenProvider;

    private final NoticeService noticeService;

    private final S3Uploader s3Uploader;



    @GetMapping("/list/notice")
    public ResponseEntity<List<NoticeRequest>> getNotices() {

        return ResponseEntity.ok(noticeService.getAllNotices());
    }


    @GetMapping("/list/one/notice/{id}")
    public ResponseEntity<NoticeRequest> getNoticeOne(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getOneNotice(id));
    }


    @PostMapping("/add/notice")
    public void createNotice(@RequestHeader(value = "Authorization") String token,
                             @ModelAttribute NoticeRequest noticeDTO,
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

    private void processFileUpload(MultipartFile file, NoticeRequest noticeDTO, String  userId) throws IOException {
        if (file != null && !file.isEmpty()) {
            log.info("File upload started");
            noticeDTO.setImageURL(s3Uploader.upload(file,userId));
        }
    }
}

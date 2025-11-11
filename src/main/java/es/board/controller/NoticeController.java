package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
//import es.board.config.s3.S3Uploader;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")

public class NoticeController {

    private final JwtTokenProvider jwtTokenProvider;

    private final NoticeService noticeService;

//    private final S3Uploader s3Uploader;



    @GetMapping("/notice")
    public ResponseEntity<?> getNotices() {
        NoticeDTO.Request latestNotice = noticeService.getLatestNotice();
        return ResponseEntity.ok(Map.of("notice",latestNotice));
    }


    @GetMapping("/notice/detail/{id}")
    public ResponseEntity<?> getNoticeOne(@PathVariable Long id) {
        NoticeDTO.Request detailNotice = noticeService.getDetailNotice(id);
        log.info(detailNotice.toString());
        return ResponseEntity.ok(Map.of("detail",detailNotice));
    }


    @PostMapping("/add/notice")
    public void createNotice(@RequestHeader(value = "Authorization") String token,
                             @ModelAttribute NoticeDTO.Request noticeDTO,
                             @RequestParam(required = false, value = "imageFile") MultipartFile file){
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId=jwtTokenProvider.getUserId(token);
//                processFileUpload(file, noticeDTO,userId);
                noticeService.createNotice(token,noticeDTO);
            }
        }
    }

//    private void processFileUpload(MultipartFile file, NoticeDTO noticeDTO, String  userId) throws IOException {
//        if (file != null && !file.isEmpty()) {
//            log.info("File upload started");
//            noticeDTO.setImageURL(s3Uploader.upload(file,userId));
//        }
//    }
}

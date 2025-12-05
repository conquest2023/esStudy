package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
//import es.board.config.s3.S3Uploader;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.entity.PostDomainMapper;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")

public class NoticeController {

    private final JwtTokenProvider jwtTokenProvider;

    private final NoticeService noticeService;




    @GetMapping("/notice")
    public ResponseEntity<?> getNotices() {
        PostDTO.Response latestNotice = noticeService.getLatestNotice();
        return ResponseEntity.ok(Map.of("notice",latestNotice));
    }


    @GetMapping("/notice/detail/{id}")
    public ResponseEntity<?> getNoticeOne(@PathVariable Long id) {
        PostDTO.Response detailNotice = noticeService.getDetailNotice(id);
        return ResponseEntity.ok(Map.of("detail",detailNotice));
    }


    @GetMapping("/notices")
    public ResponseEntity<?> getPagingNotice(@RequestParam String category,
                                             @RequestParam int page,
                                             @RequestParam int size){
        Page<PostEntity> p = noticeService.getNoticeList(page, size, category);

        List<PostDTO.Response> responseList = PostDomainMapper.toResponse(p);
        return ResponseEntity.ok(Map.of(
                "content", responseList,
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalPages", p.getTotalPages(),
                "totalElements", p.getTotalElements(),
                "last", p.isLast()
        ));
    }


    @PostMapping("/notice")
    public void createNotice(@RequestAttribute("userId") String userId,
                             NoticeDTO.Request noticeDTO,
                             @RequestParam(required = false, value = "imageFile") MultipartFile file){


                noticeService.createNotice(userId,noticeDTO);

            }
    }

//    private void processFileUpload(MultipartFile file, NoticeDTO noticeDTO, String  userId) throws IOException {
//        if (file != null && !file.isEmpty()) {
//            log.info("File upload started");
//            noticeDTO.setImageURL(s3Uploader.upload(file,userId));
//        }
//    }


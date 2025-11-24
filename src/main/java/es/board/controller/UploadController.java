package es.board.controller;

import es.board.config.s3.S3Uploader;
import es.board.config.s3.TemporaryS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadController {

    private final TemporaryS3Uploader s3Uploader;

    @PostMapping(path = "/upload-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height
    ) {
        try {
            String url;
            log.info("[UPLOAD] name={}, size={}, ct={}",
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
            if (width != null && height != null && width > 0 && height > 0) {
                url = s3Uploader.uploadResized(file, Math.max(width, 1), Math.max(height, 1), 0.82f);
                log.info("resize={}",url);
            } else {
                url = s3Uploader.upload(file);
                log.info("noResize={}",url);

            }
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Upload failed",
                    "message", e.getMessage()
            ));
        }
    }
}

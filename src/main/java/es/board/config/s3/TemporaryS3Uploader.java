package es.board.config.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemporaryS3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucketname}")
    private String bucket;

    public String upload(MultipartFile file) {
        String key = "feeds/raw/" + UUID.randomUUID() + "_" + safe(file.getOriginalFilename());

        try (InputStream in = file.getInputStream()) {
            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .cacheControl("public, max-age=31536000, immutable")
                    .build();

            long size = file.getSize();
            if (size > 0) {
                s3Client.putObject(put, RequestBody.fromInputStream(in, size));
            } else {
                // 일부 환경에서 size가 0으로 오는 이슈 방지
                byte[] bytes = file.getBytes();
                s3Client.putObject(put, RequestBody.fromBytes(bytes));
            }
            return publicUrl(key);
        } catch (IOException e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }


    public String uploadResized(MultipartFile file, int maxW, int maxH, float quality) {
        String key = "feeds/resized/" + UUID.randomUUID() + ".jpg";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .size(maxW, maxH)
                    .outputQuality(quality)   // 0.0 ~ 1.0
                    .outputFormat("jpg")
                    .toOutputStream(out);

            byte[] bytes = out.toByteArray();

            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("image/jpeg")
                    .cacheControl("public, max-age=31536000, immutable")
                    .build();

            s3Client.putObject(put, RequestBody.fromBytes(bytes));
            return publicUrl(key);
        } catch (IOException e) {
            throw new RuntimeException("S3 resize upload failed", e);
        }
    }

    // === 퍼블릭 URL 생성 ===
    private String publicUrl(String key) {
        // SDK v2의 utilities 사용 (리전/엔드포인트에 안전)
        return s3Client.utilities()
                .getUrl(b -> b.bucket(bucket).key(key))
                .toExternalForm();

        // 또는 고정 포맷:
        // return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    // 파일명 정리
    private String safe(String name) {
        return (name == null ? "file" : name).replaceAll("[\\s\\\\/]+", "_");
    }
}

package es.board.config.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public List<String> upload(List<MultipartFile> files) {
        if (files.size()>3){
            throw new RuntimeException("사진은 최대 3개 가능합니다");
        }
        // 각 파일을 업로드하고 url을 리스트로 반환
        return files.stream()
                .map(this::uploadImage)
                .toList();
    }

    // [private 메서드] validateFile메서드를 호출하여 유효성 검증 후 uploadImageToS3메서드에 데이터를 반환하여 S3에 파일 업로드, public url을 받아 서비스 로직에 반환
    private String uploadImage(MultipartFile file) {
        validateFile(file.getOriginalFilename()); // 파일 유효성 검증
        return uploadImageToS3(file); // 이미지를 S3에 업로드하고, 저장된 파일의 public url을 서비스 로직에 반환
    }

    // [private 메서드] 파일 유효성 검증
    private void validateFile(String filename) {
        // 파일 존재 유무 검증
        if (filename == null || filename.isEmpty()) {
            throw new RuntimeException("지원되지 않는 형식입니다.");
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new RuntimeException("지원되지 않는 형식입니다.");
        }

        // 허용되지 않는 확장자 검증
        String extension = URLConnection.guessContentTypeFromName(filename);
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");
        if (extension == null || !allowedExtentionList.contains(extension)) {
            throw new RuntimeException("지원되지 않는 형식입니다.");
        }
    }

    // [private 메서드] 직접적으로 S3에 업로드
    private String uploadImageToS3(MultipartFile file) {
        // 원본 파일 명
        String originalFilename = file.getOriginalFilename();
        // 확장자 명
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        // 변경된 파일
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;

        // 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = file.getInputStream()) {
            // PutObjectRequest 객체 생성
           PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3FileName) // 저장할 파일 이름
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType("image/" + extension)
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new RuntimeException("지원되지 않는 형식입니다.");
        }

        return s3Client.utilities().getUrl(url -> url.bucket(bucketName).key(s3FileName)).toString();
    }
}

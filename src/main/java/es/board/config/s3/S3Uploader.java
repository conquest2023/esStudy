package es.board.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName,int width) throws IOException {
        File uploadFile = convert(multipartFile,width)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName,width);
    }

    private String upload(File uploadFile, String dirName, int width) {
        String fileName = dirName + "/" + changedImageName(uploadFile.getName());
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile); // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
    }

    // 실질적인 s3 업로드 부분
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        log.info(targetFile.toString());
        if (targetFile.delete()) {
            log.info("데스크탑 파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file,int width) throws IOException {
        log.info(String.valueOf(width));
//        log.info(String.valueOf(height));
        String extension = getExtension(file.getOriginalFilename());
        String safeName = UUID.randomUUID() + "." + extension; // ← 영문 안전 이름

        File convertFile = new File(safeName);
//        if (convertFile.createNewFile()) {
            try (InputStream in = file.getInputStream()) {
                Thumbnails.of(in)
                        .size(width, Integer.MAX_VALUE) // 비율 유지하고 너비만 고정
                        .outputFormat(extension)        // 포맷 유지 (예: jpg, png)
                        .toFile(convertFile);
                return Optional.of(convertFile);
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }



    // 랜덤 파일 이름 메서드 (파일 이름 중복 방지)
    private String changedImageName(String originName) {
        String random = UUID.randomUUID().toString();
        return random + originName;
    }
    public void deleteFile(String fileUrl) {
        try {
            String fileKey = extractFileKey(fileUrl);
            amazonS3Client.deleteObject(bucket, fileKey);
            log.info("S3 삭제 성공: {}", fileKey);
        } catch (Exception e) {
            log.error("S3 삭제 실패: {}", fileUrl, e);
        }
    }
    private String getExtension(String filename)
    {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    private String extractFileKey(String fileUrl) {

        return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
    }
}

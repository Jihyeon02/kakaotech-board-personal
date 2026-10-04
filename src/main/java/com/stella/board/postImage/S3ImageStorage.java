package com.stella.board.postImage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Component
public class S3ImageStorage {

    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3ImageStorage(
            AmazonS3 amazonS3,
            @Value("${cloud.aws.s3.bucket}") String bucket
    ) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    public S3UploadResult upload(
            Long postId,
            MultipartFile file
    ) throws IOException {

        String originalFileName = getOriginalFileName(file);
        String imageKey = createImageKey(postId, originalFileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(
                    bucket,
                    imageKey,
                    inputStream,
                    metadata
            );
        }

        String imageUrl = amazonS3
                .getUrl(bucket, imageKey)
                .toString();

        return new S3UploadResult(imageKey, imageUrl);
    }

    public void delete(String imageKey) {
        amazonS3.deleteObject(bucket, imageKey);
    }

    public void deleteQuietly(String imageKey) {
        try {
            amazonS3.deleteObject(bucket, imageKey);
        } catch (Exception exception) {
            // 실제 운영에서는 로그 및 삭제 재처리 큐에 기록
        }
    }

    private String createImageKey(
            Long postId,
            String originalFileName
    ) {
        String extension = getExtension(originalFileName);

        return "posts/"
                + postId
                + "/"
                + UUID.randomUUID()
                + extension;
    }

    private String getOriginalFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            return "unknown";
        }

        String normalizedName = originalFileName.replace("\\", "/");

        return normalizedName.substring(
                normalizedName.lastIndexOf("/") + 1
        );
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1) {
            return "";
        }

        return fileName
                .substring(dotIndex)
                .toLowerCase(Locale.ROOT);
    }

    public record S3UploadResult(
            String imageKey,
            String imageUrl
    ) {
    }
}

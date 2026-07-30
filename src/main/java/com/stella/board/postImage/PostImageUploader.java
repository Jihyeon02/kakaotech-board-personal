package com.stella.board.postImage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostImageUploader {

    private final S3ImageStorage s3ImageStorage;

    public PostImageUploader(
            S3ImageStorage s3ImageStorage
    ) {
        this.s3ImageStorage = s3ImageStorage;
    }

    public List<UploadedImage> uploadAll(
            Long postId,
            List<MultipartFile> files
    ) {
        List<UploadedImage> uploadedImages =
                new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                S3ImageStorage.S3UploadResult result =
                        s3ImageStorage.upload(postId, file);

                uploadedImages.add(
                        new UploadedImage(
                                result.imageKey(),
                                result.imageUrl(),
                                getOriginalFileName(file),
                                file.getSize(),
                                file.getContentType()
                        )
                );
            }

            return uploadedImages;

        } catch (Exception exception) {
            uploadedImages.stream()
                    .map(UploadedImage::imageKey)
                    .forEach(s3ImageStorage::deleteQuietly);

            throw new PostImageUploadException(
                    "이미지 업로드에 실패했습니다.",
                    exception
            );
        }
    }

    private String getOriginalFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            return "unknown";
        }

        String normalized = fileName.replace("\\", "/");

        return normalized.substring(
                normalized.lastIndexOf("/") + 1
        );
    }

    public record UploadedImage(
            String imageKey,
            String imageUrl,
            String originalFileName,
            long fileSize,
            String contentType
    ) {
    }
}
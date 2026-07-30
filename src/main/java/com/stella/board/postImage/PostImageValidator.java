package com.stella.board.postImage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Component
public class PostImageValidator {

    private static final int MAX_FILE_COUNT = 30;
    private static final long MAX_FILE_SIZE =
            20L * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/gif",
                    "image/webp"
            );

    public void validate(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidPostImageException(
                    "업로드할 이미지가 없습니다."
            );
        }

        if (files.size() > MAX_FILE_COUNT) {
            throw new InvalidPostImageException(
                    "이미지는 한 번에 최대 30개까지 업로드할 수 있습니다."
            );
        }

        for (MultipartFile file : files) {
            validateFile(file);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPostImageException(
                    "빈 파일은 업로드할 수 없습니다."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidPostImageException(
                    "이미지 한 장은 20MB를 초과할 수 없습니다."
            );
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidPostImageException(
                    "지원하지 않는 이미지 형식입니다."
            );
        }
    }
}

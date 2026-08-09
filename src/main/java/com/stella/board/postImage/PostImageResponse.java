package com.stella.board.postImage;

public record PostImageResponse(
        Long imageId,
        String imageUrl,
        String originalFileName,
        long imageFileSize,
        String contentType,
        int sortOrder
) {
    public static PostImageResponse from(PostImage image) {
        return new PostImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getOriginalFileName(),
                image.getImageFileSize(),
                image.getContentType(),
                image.getSortOrder()
        );
    }
}
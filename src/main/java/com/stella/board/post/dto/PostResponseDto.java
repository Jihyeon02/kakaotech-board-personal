package com.stella.board.post.dto;

import com.stella.board.post.Post;
import com.stella.board.postImage.PostImageResponse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record PostResponseDto(
        Long postId,
        Long userId,
        String title,
        String summary,
        String content,
        String thumbnailUrl,
        List<PostImageResponse> images,
        LocalDateTime createdTime,
        LocalDateTime updatedTime
) {
    public static PostResponseDto from(Post post) {
        List<PostImageResponse> images = post.getImages()
                .stream()
                .sorted(Comparator.comparingInt(
                        image -> image.getSortOrder()
                ))
                .map(PostImageResponse::from)
                .toList();

        return new PostResponseDto(
                post.getPostId(),
                post.getUserId(),
                post.getTitle(),
                post.getSummary(),
                post.getContent(),
                post.getThumbnailUrl(),
                images,
                post.getCreatedTime(),
                post.getUpdatedTime()
        );
    }
}
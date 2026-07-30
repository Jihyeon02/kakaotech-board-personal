package com.stella.board.post.dto;

import com.stella.board.post.Post;

import java.time.LocalDateTime;

public record PostListResponseDto(
        Long postId,
        Long userId,
        String title,
        String summary,
        String thumbnailUrl,
        LocalDateTime createdTime
) {
    public static PostListResponseDto from(Post post) {
        return new PostListResponseDto(
                post.getPostId(),
                post.getUserId(),
                post.getTitle(),
                post.getSummary(),
                post.getThumbnailUrl(),
                post.getCreatedTime()
        );
    }
}
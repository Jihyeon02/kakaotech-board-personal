package com.stella.board.post.dto;

import com.stella.board.post.Post;
import com.stella.board.user.User;

import java.time.LocalDateTime;

public record PostListResponseDto(
        Long postId,
        Long userId,
        String nickname,
        String profileImageUrl,
        String title,
        String summary,
        String thumbnailUrl,
        LocalDateTime createdTime
) {
    public static PostListResponseDto from(
            Post post,
            User author,
            String firstImageUrl
    ) {
        return new PostListResponseDto(
                post.getPostId(),
                post.getUserId(),
                author.getNickname(),
                author.getProfile_imageUrl(),
                post.getTitle(),
                post.getSummary(),
                firstImageUrl != null
                        ? firstImageUrl
                        : post.getThumbnailUrl(),
                post.getCreatedTime()
        );
    }
}

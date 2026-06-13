package com.stella.board.post.dto;

import com.stella.board.Image;
import com.stella.board.post.Post;
import com.stella.board.user.User;

import java.util.List;

public record PostResponseDto(Long postId, Long user, String title, List<Image> image, String content) {
    public static PostResponseDto of(Post post) {
        return new PostResponseDto(post.getPostId(), post.getUser(), post.getTitle(), post.getImages(), post.getContent());
    }
}
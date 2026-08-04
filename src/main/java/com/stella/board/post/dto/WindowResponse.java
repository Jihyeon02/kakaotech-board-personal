package com.stella.board.post.dto;

import com.stella.board.post.Post;

import java.util.List;

public record WindowResponse(List<Post> posts, boolean hasNext, Long nextCursor) {
}

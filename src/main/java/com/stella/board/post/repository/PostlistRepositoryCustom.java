package com.stella.board.post.repository;

import com.stella.board.post.Post;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Window;

public interface PostlistRepositoryCustom {
    Long findLastPostId();
}
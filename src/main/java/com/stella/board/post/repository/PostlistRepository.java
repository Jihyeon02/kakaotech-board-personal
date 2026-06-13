package com.stella.board.post.repository;

import com.stella.board.post.Post;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface PostlistRepository extends Repository<Post, Long> {
    Window<Post> findFirst10ByOrderByIdDesc(KeysetScrollPosition position);

}



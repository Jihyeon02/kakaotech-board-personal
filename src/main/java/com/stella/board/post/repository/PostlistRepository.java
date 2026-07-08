package com.stella.board.post.repository;

import com.stella.board.post.Post;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.repository.Repository;

public interface PostlistRepository extends Repository<Post, Long>, PostlistRepositoryCustom {

    // derived query로 자동 구현됨 (QueryDSL 불필요)
    Window<Post> findFirst10ByOrderByPostIdDesc(KeysetScrollPosition position);
}





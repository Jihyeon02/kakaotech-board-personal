package com.stella.board.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.post.Post;
import com.stella.board.post.QPost;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Window;

public class PostlistRepositoryImpl implements PostlistRepository {

    private final JPAQueryFactory queryFactory;

    public PostlistRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Long findLastPostId() {
        return queryFactory
                .select(QPost.post.postId.max())
                .from(QPost.post)
                .fetchOne();
    }

    @Override
    public Window<Post> findFirst10ByOrderByIdDesc(KeysetScrollPosition position) {
        return null;
    }
}

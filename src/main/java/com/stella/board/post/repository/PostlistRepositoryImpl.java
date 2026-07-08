package com.stella.board.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.post.Post;
import com.stella.board.post.QPost;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Repository;

@Repository
public class PostlistRepositoryImpl implements PostlistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostlistRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Long findLastPostId() {
        return queryFactory
                .select(QPost.post.postId.max())
                .from(QPost.post)
                .fetchOne();
    }
}

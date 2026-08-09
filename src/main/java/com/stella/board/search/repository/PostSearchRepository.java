package com.stella.board.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.post.dto.PostListResponseDto;
import com.stella.board.postImage.QPostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.stella.board.post.QPost.post;
import static com.stella.board.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostSearchRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<PostListResponseDto> search(
            String keyword,
            Long authorId,
            Pageable pageable
    ) {
        int pageSize = pageable.getPageSize();
        QPostImage firstImage = new QPostImage("firstImage");
        QPostImage imageForMinSort = new QPostImage("imageForMinSort");

        List<PostListResponseDto> results = queryFactory
                .select(Projections.constructor(
                        PostListResponseDto.class,
                        post.postId,
                        post.userId,
                        user.nickname,
                        user.profile_imageUrl,
                        post.title,
                        post.summary,
                        firstImage.imageUrl.coalesce(post.thumbnailUrl),
                        post.createdTime
                ))
                .from(post)
                .leftJoin(user)
                .on(user.user_id.eq(post.userId))
                .leftJoin(firstImage)
                .on(
                        firstImage.post.postId.eq(post.postId)
                                .and(firstImage.sortOrder.eq(
                                        JPAExpressions
                                                .select(imageForMinSort.sortOrder.min())
                                                .from(imageForMinSort)
                                                .where(
                                                        imageForMinSort.post.postId
                                                                .eq(post.postId)
                                                )
                                ))
                )
                .where(
                        containsKeyword(keyword),
                        authoredBy(authorId)
                )
                .orderBy(
                        matchGrade(keyword).asc(),
                        post.createdTime.desc(),
                        post.postId.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();

        return toSlice(results, pageable);
    }

    private BooleanExpression containsKeyword(String keyword) {
        return post.title.contains(keyword)
                .or(post.content.contains(keyword));
    }

    private BooleanExpression authoredBy(Long authorId) {
        if (authorId == null) {
            return null;
        }

        return post.userId.eq(authorId);
    }

    private NumberExpression<Integer> matchGrade(String keyword) {
        return new CaseBuilder()
                .when(post.title.contains(keyword)).then(1)
                .otherwise(2);
    }

    private Slice<PostListResponseDto> toSlice(
            List<PostListResponseDto> results,
            Pageable pageable
    ) {
        int pageSize = pageable.getPageSize();
        boolean hasNext = results.size() > pageSize;

        List<PostListResponseDto> content = hasNext
                ? List.copyOf(results.subList(0, pageSize))
                : List.copyOf(results);

        return new SliceImpl<>(content, pageable, hasNext);
    }
}

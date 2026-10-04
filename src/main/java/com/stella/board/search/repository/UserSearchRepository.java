package com.stella.board.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.search.dto.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.stella.board.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserSearchRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<UserSearchResponse> searchByNickname(
            String keyword,
            Pageable pageable
    ) {
        int pageSize = pageable.getPageSize();

        List<UserSearchResponse> results = queryFactory
                .select(Projections.constructor(
                        UserSearchResponse.class,
                        user.user_id,
                        user.nickname,
                        user.profile_imageUrl
                ))
                .from(user)
                .where(user.nickname.contains(keyword))
                .orderBy(
                        matchGrade(keyword).asc(),
                        user.user_id.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();

        return toSlice(results, pageable);
    }

    private NumberExpression<Integer> matchGrade(String keyword) {
        return new CaseBuilder()
                .when(user.nickname.eq(keyword)).then(1)
                .when(user.nickname.startsWith(keyword)).then(2)
                .otherwise(3);
    }

    private Slice<UserSearchResponse> toSlice(
            List<UserSearchResponse> results,
            Pageable pageable
    ) {
        int pageSize = pageable.getPageSize();
        boolean hasNext = results.size() > pageSize;

        List<UserSearchResponse> content = hasNext
                ? List.copyOf(results.subList(0, pageSize))
                : List.copyOf(results);

        return new SliceImpl<>(content, pageable, hasNext);
    }
}

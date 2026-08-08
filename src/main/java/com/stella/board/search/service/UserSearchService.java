package com.stella.board.search.service;

import com.stella.board.search.dto.UserSearchResponse;
import com.stella.board.search.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSearchService {

    private static final int MAX_PAGE_SIZE = 50;

    private final UserSearchRepository userSearchRepository;

    public Slice<UserSearchResponse> searchUsers(
            String keyword,
            Pageable pageable
    ) {
        String normalizedKeyword = normalize(keyword);
        validatePageSize(pageable);

        return userSearchRepository.searchByNickname(
                normalizedKeyword,
                pageable
        );
    }

    private String normalize(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "검색어는 공백일 수 없습니다."
            );
        }

        return keyword.trim();
    }

    private void validatePageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "검색 결과는 한 번에 최대 50개까지 조회할 수 있습니다."
            );
        }
    }
}

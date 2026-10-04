package com.stella.board.search.controller;

import com.stella.board.search.dto.UserSearchResponse;
import com.stella.board.search.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserSearchController {

    private final UserSearchService userSearchService;

    @GetMapping("/search")
    public Slice<UserSearchResponse> search(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return userSearchService.searchUsers(keyword, pageable);
    }
}

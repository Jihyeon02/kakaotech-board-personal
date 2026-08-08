package com.stella.board.search.controller;

import com.stella.board.post.dto.PostListResponseDto;
import com.stella.board.search.service.PostSearchService;
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
@RequestMapping("/posts")
public class PostSearchController {

    private final PostSearchService postSearchService;

    @GetMapping("/search")
    public Slice<PostListResponseDto> search(
            @RequestParam String keyword,
            @RequestParam(required = false) Long authorId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return postSearchService.searchPosts(
                keyword,
                authorId,
                pageable
        );
    }
}

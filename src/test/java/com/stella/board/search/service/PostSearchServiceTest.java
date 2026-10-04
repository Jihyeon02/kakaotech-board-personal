package com.stella.board.search.service;

import com.stella.board.search.repository.PostSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSearchServiceTest {

    @Mock
    private PostSearchRepository postSearchRepository;

    @InjectMocks
    private PostSearchService postSearchService;

    @Test
    void 검색어를_정규화하고_작성자_조건을_전달한다() {
        Pageable pageable = PageRequest.of(0, 20);

        when(postSearchRepository.search("스프링", 10L, pageable))
                .thenReturn(new SliceImpl<>(List.of(), pageable, false));

        postSearchService.searchPosts(
                "  스프링  ",
                10L,
                pageable
        );

        verify(postSearchRepository)
                .search("스프링", 10L, pageable);
    }

    @Test
    void 전체_게시글_검색은_작성자_조건을_null로_전달한다() {
        Pageable pageable = PageRequest.of(0, 20);

        when(postSearchRepository.search("스프링", null, pageable))
                .thenReturn(new SliceImpl<>(List.of(), pageable, false));

        postSearchService.searchPosts(
                "스프링",
                null,
                pageable
        );

        verify(postSearchRepository)
                .search("스프링", null, pageable);
    }

    @Test
    void 공백_검색어는_거부한다() {
        Pageable pageable = PageRequest.of(0, 20);

        assertThrows(
                IllegalArgumentException.class,
                () -> postSearchService.searchPosts(
                        "   ",
                        null,
                        pageable
                )
        );

        verifyNoInteractions(postSearchRepository);
    }

    @Test
    void 페이지_크기가_50을_초과하면_거부한다() {
        Pageable pageable = PageRequest.of(0, 51);

        assertThrows(
                IllegalArgumentException.class,
                () -> postSearchService.searchPosts(
                        "스프링",
                        null,
                        pageable
                )
        );
    }
}

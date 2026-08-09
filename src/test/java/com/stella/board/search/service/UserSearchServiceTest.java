package com.stella.board.search.service;

import com.stella.board.search.repository.UserSearchRepository;
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
class UserSearchServiceTest {

    @Mock
    private UserSearchRepository userSearchRepository;

    @InjectMocks
    private UserSearchService userSearchService;

    @Test
    void 검색어의_앞뒤_공백을_제거한다() {
        Pageable pageable = PageRequest.of(0, 20);

        when(userSearchRepository.searchByNickname("민수", pageable))
                .thenReturn(new SliceImpl<>(List.of(), pageable, false));

        userSearchService.searchUsers("  민수  ", pageable);

        verify(userSearchRepository)
                .searchByNickname("민수", pageable);
    }

    @Test
    void 공백_검색어는_거부한다() {
        Pageable pageable = PageRequest.of(0, 20);

        assertThrows(
                IllegalArgumentException.class,
                () -> userSearchService.searchUsers("   ", pageable)
        );

        verifyNoInteractions(userSearchRepository);
    }

    @Test
    void 페이지_크기가_50을_초과하면_거부한다() {
        Pageable pageable = PageRequest.of(0, 51);

        assertThrows(
                IllegalArgumentException.class,
                () -> userSearchService.searchUsers("민수", pageable)
        );
    }
}

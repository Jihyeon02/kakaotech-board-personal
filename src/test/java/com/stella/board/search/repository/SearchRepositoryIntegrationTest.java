package com.stella.board.search.repository;

import com.stella.board.post.Post;
import com.stella.board.post.dto.PostListResponseDto;
import com.stella.board.post.repository.PostRepository;
import com.stella.board.search.dto.UserSearchResponse;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class SearchRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserSearchRepository userSearchRepository;

    @Autowired
    private PostSearchRepository postSearchRepository;

    @Test
    void 사용자_검색은_정확_접두어_부분_일치_순으로_정렬한다() {
        String keyword = uniqueKeyword();

        User exact = saveUser(keyword, "exact");
        User prefix = saveUser(keyword + "앞", "prefix");
        User contains = saveUser("뒤" + keyword, "contains");

        Slice<UserSearchResponse> result =
                userSearchRepository.searchByNickname(
                        keyword,
                        PageRequest.of(0, 20)
                );

        List<Long> userIds = result.getContent()
                .stream()
                .map(UserSearchResponse::userId)
                .toList();

        assertEquals(
                List.of(
                        exact.getUser_id(),
                        prefix.getUser_id(),
                        contains.getUser_id()
                ),
                userIds
        );
    }

    @Test
    void 게시글_검색은_제목을_본문보다_우선하고_작성자로_필터링한다() {
        String keyword = uniqueKeyword();
        Long targetAuthorId = 101L;

        Post titleMatched = postRepository.save(
                Post.create(
                        targetAuthorId,
                        keyword + " 제목",
                        "제목 일치 게시글",
                        "검색어가 없는 본문"
                )
        );

        Post contentMatched = postRepository.save(
                Post.create(
                        targetAuthorId,
                        "일반 제목",
                        "본문 일치 게시글",
                        "본문에 " + keyword + " 포함"
                )
        );

        postRepository.save(
                Post.create(
                        202L,
                        keyword + " 다른 작성자",
                        "다른 작성자 게시글",
                        "검색어가 없는 본문"
                )
        );

        Slice<PostListResponseDto> result =
                postSearchRepository.search(
                        keyword,
                        targetAuthorId,
                        PageRequest.of(0, 20)
                );

        List<PostListResponseDto> posts = result.getContent();

        assertEquals(2, posts.size());
        assertEquals(titleMatched.getPostId(), posts.get(0).postId());
        assertEquals(contentMatched.getPostId(), posts.get(1).postId());
        assertTrue(
                posts.stream()
                        .allMatch(post ->
                                post.userId().equals(targetAuthorId)
                        )
        );
    }

    private User saveUser(String nickname, String emailPrefix) {
        String email = emailPrefix
                + "-"
                + UUID.randomUUID()
                + "@test.com";

        return userRepository.save(
                new User(
                        null,
                        email,
                        "password123",
                        nickname,
                        null
                )
        );
    }

    private String uniqueKeyword() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 4);
    }
}

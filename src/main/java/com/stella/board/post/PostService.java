package com.stella.board.post;

import com.stella.board.global.exception.AuthorizedException;
import com.stella.board.post.dto.PostRequestDto;
import com.stella.board.post.dto.PostResponseDto;
import com.stella.board.post.dto.WindowResponse;
import com.stella.board.post.repository.PostRepository;
import com.stella.board.post.repository.PostlistRepository;
import com.stella.board.post.repository.PostlistRepositoryImpl;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {
    // Service 코드에서 해야할 일
    // Controller 에서 Dto로 변환하여 Service에 넘겨준 데이터를 받는다
    // 각 HTTP Method 별로 메소드를 만들어 필요한 처리를 정의한다. - 레포지토리에 넘기고, 응답 DTO에 값 넣고
    // Entity로 값을 넣어서 Repository에 보내기도 해야함 (DB접근)

    private final PostRepository postRepository;
    private final PostlistRepository postlistRepository;
    private final UserRepository userRepository;
    private final PostlistRepositoryImpl postlistRepositoryImpl;

    public PostService(PostRepository postRepository, PostlistRepository postlistRepository, UserRepository userRepository, PostlistRepositoryImpl postlistRepositoryImpl) {
        this.postRepository = postRepository;
        this.postlistRepository = postlistRepository;
        this.userRepository = userRepository;
        this.postlistRepositoryImpl = postlistRepositoryImpl;
    }

    // 목록 조회 - 페이징 처리도 고려해서 구현해야 함.
    public WindowResponse findAllPosts(Long last_post_id) {

        if (last_post_id == null) {
            last_post_id = postlistRepositoryImpl.findLastPostId();
        }
        // 메인 로직
        KeysetScrollPosition position = ScrollPosition.forward(Map.of("id", last_post_id));
        Window<Post> window = postlistRepository.findFirst10ByOrderByIdDesc(position);
        List<Post> posts = window.getContent();

        boolean hasNext = window.hasNext();
        // 다음 게시물을 가리키도록
        Long nextCursor = hasNext
                ? posts.get(posts.size() - 1).getPostId()
                : null;

        return new WindowResponse(posts, hasNext, nextCursor);
    }
    // 상세 조회
    public PostResponseDto findOnePost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        return PostResponseDto.of(post);
    }

    // 작성
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        Long userId = postRequestDto.getUserId();
        Post post = new Post(null, userId, postRequestDto.getTitle(),postRequestDto.getImages(), postRequestDto.getContent(), LocalDateTime.now());
        postRepository.save(post);
        return PostResponseDto.of(post);
    }

    // 수정
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto) {
        Optional<Post> oldPost = postRepository.findById(postId);
        //postRequestDto -> Post
        Post post = new Post(postId, oldPost.get().getUser(), postRequestDto.getTitle(),postRequestDto.getImages(), postRequestDto.getContent(), oldPost.get().getCreatedTime());
        return PostResponseDto.of(postRepository.save(post));
    }
    // 삭제
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }



}

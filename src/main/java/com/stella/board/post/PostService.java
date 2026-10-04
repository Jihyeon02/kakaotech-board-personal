package com.stella.board.post;

import com.stella.board.post.dto.PostRequestDto;
import com.stella.board.post.dto.PostListResponseDto;
import com.stella.board.post.dto.PostResponseDto;
import com.stella.board.post.repository.PostRepository;
import com.stella.board.postImage.PostImage;
import com.stella.board.postImage.PostImageRepository;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            PostImageRepository postImageRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postImageRepository = postImageRepository;
    }

    public PostResponseDto findOnePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다. postId=" + postId
                        )
                );

        return PostResponseDto.from(
                post,
                findAuthor(post.getUserId())
        );
    }

    public Slice<PostListResponseDto> findPosts(Pageable pageable) {
        Slice<Post> posts = postRepository
                .findAllByOrderByCreatedTimeDescPostIdDesc(pageable);

        List<Long> postIds = posts.getContent().stream()
                .map(Post::getPostId)
                .toList();

        Map<Long, User> authors = userRepository.findAllById(
                        posts.getContent().stream()
                                .map(Post::getUserId)
                                .distinct()
                                .toList()
                ).stream()
                .collect(Collectors.toMap(
                        User::getUser_id,
                        Function.identity()
                ));

        Map<Long, String> firstImageUrls = postIds.isEmpty()
                ? Map.of()
                : postImageRepository.findFirstImagesByPostIds(postIds)
                        .stream()
                        .collect(Collectors.toMap(
                                image -> image.getPost().getPostId(),
                                PostImage::getImageUrl,
                                (first, ignored) -> first
                        ));

        return posts.map(post -> PostListResponseDto.from(
                post,
                requireAuthor(authors, post.getUserId()),
                firstImageUrls.get(post.getPostId())
        ));
    }

    @Transactional
    public PostResponseDto createPost(
            PostRequestDto request
    ) {
        User author = findAuthor(request.userId());

        Post post = Post.create(
                request.userId(),
                request.title(),
                request.summary(),
                request.content()
        );

        Post savedPost = postRepository.save(post);

        return PostResponseDto.from(savedPost, author);
    }

    @Transactional
    public PostResponseDto updatePost(
            Long postId,
            PostRequestDto request
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다. postId=" + postId
                        )
                );

        post.changeContent(
                request.title(),
                request.summary(),
                request.content()
        );

        // 영속 상태이므로 save()를 다시 호출하지 않아도 dirty checking
        return PostResponseDto.from(
                post,
                findAuthor(post.getUserId())
        );
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다. postId=" + postId
                        )
                );

        postRepository.delete(post);
    }

    private User findAuthor(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다. userId=" + userId
                        )
                );
    }

    private User requireAuthor(
            Map<Long, User> authors,
            Long userId
    ) {
        User author = authors.get(userId);

        if (author == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다. userId=" + userId
            );
        }

        return author;
    }
}

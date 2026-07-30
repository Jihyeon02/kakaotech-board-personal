package com.stella.board.post;

import com.stella.board.post.dto.PostRequestDto;
import com.stella.board.post.dto.PostResponseDto;
import com.stella.board.post.repository.PostRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public PostService(
            PostRepository postRepository
    ) {
        this.postRepository = postRepository;
    }

    public PostResponseDto findOnePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다. postId=" + postId
                        )
                );

        return PostResponseDto.from(post);
    }

    @Transactional
    public PostResponseDto createPost(
            PostRequestDto request
    ) {
        Post post = Post.create(
                request.userId(),
                request.title(),
                request.summary(),
                request.content()
        );

        Post savedPost = postRepository.save(post);

        return PostResponseDto.from(savedPost);
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
        return PostResponseDto.from(post);
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
}
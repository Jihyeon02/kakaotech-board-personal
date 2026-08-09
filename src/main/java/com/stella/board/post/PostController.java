package com.stella.board.post;

import com.stella.board.post.dto.PostRequestDto;
import com.stella.board.post.dto.PostListResponseDto;
import com.stella.board.post.dto.PostResponseDto;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDto post(
            @Valid @RequestBody PostRequestDto postRequestDto
    ) {
        return postService.createPost(postRequestDto);
    }

    @GetMapping
    public Slice<PostListResponseDto> getPosts(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return postService.findPosts(pageable);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getOnePost(@PathVariable Long postId) {
        return postService.findOnePost(postId);
    }

    @PutMapping("/{postId}")
    public PostResponseDto updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequestDto postRequestDto
    ) {
        return postService.updatePost(postId, postRequestDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }



}

package com.stella.board.post;

import com.stella.board.post.dto.PostRequestDto;
import com.stella.board.post.dto.PostResponseDto;

import com.stella.board.post.dto.WindowResponse;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public PostResponseDto post(@RequestBody PostRequestDto postRequestDto) {
        return postService.createPost(postRequestDto);
    }

    // 목록조회 api 작성해야함.

    @GetMapping("/{postId}")
    public PostResponseDto getOnePost(@PathVariable Long postId) {
        return postService.findOnePost(postId);
    }

    @PutMapping("/{postId}")
    public PostResponseDto updatePost(@PathVariable Long postId,@RequestBody PostRequestDto postRequestDto) {
        return postService.updatePost(postId, postRequestDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }



}

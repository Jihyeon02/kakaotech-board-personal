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

    @GetMapping
    public WindowResponse getAllLists(@RequestParam(required = false) Long lastPostId) {
        return postService.findAllPosts(lastPostId);
    }

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

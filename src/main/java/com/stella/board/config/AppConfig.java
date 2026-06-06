package com.stella.board.config;

import com.stella.board.post.Post;
import com.stella.board.post.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public PostRepository postRepository() {
        return new PostRepository() {
            @Override
            public Long savePost(Post post) {
                return PostRepository.super.savePost(post);
            }

            @Override
            public List<Post> allGetPosts() {
                return PostRepository.super.allGetPosts();
            }

            @Override
            public Post getPost(Long postId) {
                return PostRepository.super.getPost(postId);
            }

            @Override
            public Post updatePost(Long postId, Post post) {
                return PostRepository.super.updatePost(postId, post);
            }

            @Override
            public void deletePost(Long postId) {
                PostRepository.super.deletePost(postId);
            }
        };
    }
}

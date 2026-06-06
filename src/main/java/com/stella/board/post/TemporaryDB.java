package com.stella.board.post;

import java.util.*;


public interface TemporaryDB {
    // 실제 데이터가 담겨질 자료형 - 실질적 임시 DB
    Map<Long,Post> tempoDb = new HashMap<>();
    Long savePost(Post post);
    void deletePost(Long postId);
    Post updatePost(Long postId, Post post);
    Post getPost(Long id);
    List<Post> allGetPosts();


}

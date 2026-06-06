package com.stella.board.post;

//import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PostRepository extends TemporaryDB { //extends JpaRepository<Post, Long>
    Long[] temp_postId = {0L};
    //public List<PostResponseDto> findAllByPostId(Long postId);
    @Override
    default Long savePost(Post post){
        ++temp_postId[0];
        tempoDb.put(temp_postId[0], post);
        return temp_postId[0];
    }

    @Override
    default void deletePost(Long postId){
        tempoDb.remove(postId);
    };

    @Override
    default Post updatePost(Long postId,Post post){
        tempoDb.put(postId, post);
        return post;
    }

    @Override
    default Post getPost(Long postId){
        return tempoDb.get(postId);
    }

    @Override
    default List<Post> allGetPosts() {
        return new ArrayList<>(tempoDb.values());
    }
}

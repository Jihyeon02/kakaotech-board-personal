//package com.stella.board.post;
//
//import com.stella.board.post.dto.PostRequestDto;
//import com.stella.board.post.dto.PostResponseDto;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class PostService {
//    // Service 코드에서 해야할 일
//    // Controller 에서 Dto로 변환하여 Service에 넘겨준 데이터를 받는다
//    // 각 HTTP Method 별로 메소드를 만들어 필요한 처리를 정의한다. - 레포지토리에 넘기고, 응답 DTO에 값 넣고
//    // Entity로 값을 넣어서 Repository에 보내기도 해야함 (DB접근)
//
//    private final PostRepository postRepository;
//
//    public PostService(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }
//
//    // 목록 조회 - List<Post>로 받아온 모든 객체를 다시 쪼개서 dto로 반환해야함...
//    public List<Post> findAllPosts() {
//        return postRepository.allGetPosts();
//    }
//    // 상세 조회
//    public PostResponseDto findOnePost(Long id) {
//        Post post = postRepository.getPost(id);
//        return new PostResponseDto(id, post);
//    }
//
//    // 작성
//    public PostResponseDto createPost(PostRequestDto postRequestDto) {
//        Post post = new Post(postRequestDto.getUserId(), postRequestDto.getTitle(), "stella",postRequestDto.getContent(), 0L, 0L, 0L);
//        return new PostResponseDto(postRepository.savePost(post),post);
//    }
//
//    // 수정
//    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto) {
//        Post oldPost = postRepository.getPost(id);
//        //postRequestDto -> Post
//        Post post = new Post(oldPost.getUserId(), postRequestDto.getTitle(), oldPost.getNickname(), postRequestDto.getContent(), 0L, 0L, 0L);
//        Post changedPost = postRepository.updatePost(id, post);
//        return findOnePost(id);
//    }
//    // 삭제
//    public void deletePost(Long id) {
//        postRepository.deletePost(id);
//    }
//
//
//
//}

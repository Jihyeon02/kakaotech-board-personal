package com.stella.board.post.dto;

import com.stella.board.post.Post;
import com.stella.board.post.PostRepository;
import com.stella.board.user.User;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class PostResponseDto {

    private Long post_id;
    private User user; // 추후에 String -> User로 변경 예정
    private String title;
    // 사진 url은 추후에 넣을 예정
    private String nickname;// 추후에 String -> User로 변경 예정
    private String content;

    private Long readCount = 0L; // 당장 개발하지 못할 기능이라 초기화
    private Long likeCount = 0L;
    private Long commentCount = 0L;
    private LocalDateTime createdTime;

    public PostResponseDto(Post post) {
        this.post_id = post.getPostId();
        this.user = post.getUser();
        this.title = post.getTitle();
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
        this.createdTime = post.getCreatedTime();
        // readCount, likeCount, commentCount는 0L로 자동 초기화
    }


}

//package com.stella.board.post;
//
////import jakarta.persistence.Entity;
////import jakarta.persistence.Id;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//@Getter
//@Entity
//public class Post {
//    @Id
//    private Long postId; // DB 달면 private으로 변경
//    private Long userId; //
//    private String title;
//    // 사진 url은 추후에 넣을 예정
//    private String nickname;// 추후에 String -> User로 변경 예정
//    private String content;
//
//    private Long readCount;
//    private Long likeCount;
//    private Long commentCount;
//    private LocalDateTime createdTime;
//
//
//    // 비즈니스 전용 생성자
//    public Post(Long userId, String title, String nickname, String content, Long readCount, Long likeCount, Long commentCount) {
//        this.userId = userId;
//        this.title = title;
//        this.nickname = nickname;
//        this.content = content;
//        this.readCount = readCount;
//        this.likeCount = likeCount;
//        this.commentCount = commentCount;
//        this.createdTime = LocalDateTime.now();
//    }
//
//}

package com.stella.board.post;
import com.stella.board.Image;
import com.stella.board.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId; // DB 달면 private으로 변경

    @Column(name = "user_id")
    private Long userId;

    @NotNull
    private String title;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @Lob
    @NotNull
    private String content;

    private LocalDateTime createdTime;


    // 비즈니스 전용 생성자
    public Post(Long postId, Long userId, String title, List<Image> images,String content, LocalDateTime createdTime) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.images = images;
        this.content = content;
        /*this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;*/
        this.createdTime = LocalDateTime.now();
    }

}

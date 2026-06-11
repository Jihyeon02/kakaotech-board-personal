package com.stella.board.post;
import com.stella.board.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Entity(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId; // DB 달면 private으로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @NotNull
    private String title;

    @Nullable
    private String imageUrl;

    @Lob
    @NotNull
    private String content;


    // 관련 엔티티 뺄지 말지 고민중이고 딱히 제약 걸만한 속성도 아니라서 일단 냅둠
    private Long readCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdTime;



    // 비즈니스 전용 생성자
    public Post(Long postId, User user, String title,String imageUrl, String content, Long readCount, Long likeCount, Long commentCount) {
        this.postId = postId;
        this.user = user;
        this.title = title;
        this.imageUrl = imageUrl;
        this.content = content;
        this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdTime = LocalDateTime.now();
    }

}

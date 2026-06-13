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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Long user;

    @NotNull
    private String title;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @Lob
    @NotNull
    private String content;


    // 관련 엔티티 뺄지 말지 고민중이고 딱히 제약 걸만한 속성도 아니라서 일단 냅둠
    private Long readCount;
    private Long likeCount;
    private Long commentCount;

    private LocalDateTime createdTime;


    // 비즈니스 전용 생성자
    public Post(Long postId, Long user, String title, List<Image> images,String content, LocalDateTime createdTime) {
        this.postId = postId;
        this.user = user;
        this.title = title;
        this.images = images;
        this.content = content;
        /*this.readCount = readCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;*/
        this.createdTime = LocalDateTime.now();
    }

}

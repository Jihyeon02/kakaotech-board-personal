package com.stella.board.post;

import com.stella.board.postImage.PostImage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String summary;

    @Column(name = "thumbnail_key", length = 500)
    private String thumbnailKey;

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PostImage> images = new ArrayList<>();

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime updatedTime;

    private Post(
            Long userId,
            String title,
            String summary,
            String content
    ) {
        this.userId = userId;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public static Post create(
            Long userId,
            String title,
            String summary,
            String content
    ) {
        return new Post(userId, title, summary, content);
    }

    public void changeContent(
            String title,
            String summary,
            String content
    ) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.updatedTime = LocalDateTime.now();
    }

    public void changeThumbnail(
            PostImage sourceImage,
            String thumbnailKey,
            String thumbnailUrl
    ) {
        if (!sourceImage.isOwnedBy(postId)) {
            throw new IllegalArgumentException(
                    "해당 게시글의 이미지가 아닙니다."
            );
        }

        if (thumbnailKey == null || thumbnailKey.isBlank()) {
            throw new IllegalArgumentException(
                    "썸네일 키는 필수입니다."
            );
        }

        if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "썸네일 URL은 필수입니다."
            );
        }

        this.thumbnailKey = thumbnailKey;
        this.thumbnailUrl = thumbnailUrl;
        this.updatedTime = LocalDateTime.now();
    }

    public void clearThumbnail() {
        this.thumbnailKey = null;
        this.thumbnailUrl = null;
        this.updatedTime = LocalDateTime.now();
    }

    public List<PostImage> getImages() {
        return Collections.unmodifiableList(images);
    }
}
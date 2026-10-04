package com.stella.board.postImage;

import com.stella.board.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "post_image",
        indexes = {
                @Index(
                        name = "idx_post_image_post_sort",
                        columnList = "post_id, sort_order"
                )
        }
)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_key", nullable = false, unique = true, length = 500)
    private String imageKey;

    @Column(name = "s3_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "image_file_size", nullable = false)
    private long imageFileSize;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public static PostImage create(
            Post post,
            String imageKey,
            String imageUrl,
            String originalFileName,
            long imageFileSize,
            String contentType,
            int sortOrder
    ) {
        return new PostImage(
                post,
                imageKey,
                imageUrl,
                originalFileName,
                imageFileSize,
                contentType,
                sortOrder
        );
    }

    public boolean isOwnedBy(Long postId) {
        return post != null
                && Objects.equals(post.getPostId(), postId);
    }

    private PostImage(
            Post post,
            String imageKey,
            String imageUrl,
            String originalFileName,
            long imageFileSize,
            String contentType,
            int sortOrder
    ) {
        this.post = Objects.requireNonNull(post);
        this.imageKey = Objects.requireNonNull(imageKey);
        this.imageUrl = Objects.requireNonNull(imageUrl);
        this.originalFileName = Objects.requireNonNull(originalFileName);
        this.imageFileSize = imageFileSize;
        this.contentType = Objects.requireNonNull(contentType);
        this.sortOrder = sortOrder;
    }
}

package com.stella.board.postImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PostImageRepository
        extends JpaRepository<PostImage, Long> {

    Optional<PostImage> findByIdAndPost_PostId(
            Long imageId,
            Long postId
    );

    List<PostImage> findAllByPost_PostIdOrderBySortOrderAsc(
            Long postId
    );

    @Query("""
            select coalesce(max(pi.sortOrder), -1)
            from PostImage pi
            where pi.post.id = :postId
            """)
    int findMaxSortOrder(@Param("postId") Long postId);
}
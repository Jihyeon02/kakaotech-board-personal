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
            select pi
            from PostImage pi
            where pi.post.postId in :postIds
              and pi.sortOrder = (
                  select min(firstImage.sortOrder)
                  from PostImage firstImage
                  where firstImage.post.postId = pi.post.postId
              )
            order by pi.post.postId asc, pi.id asc
            """)
    List<PostImage> findFirstImagesByPostIds(
            @Param("postIds") List<Long> postIds
    );

    @Query("""
            select coalesce(max(pi.sortOrder), -1)
            from PostImage pi
            where pi.post.id = :postId
            """)
    int findMaxSortOrder(@Param("postId") Long postId);
}

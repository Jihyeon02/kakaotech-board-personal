package com.stella.board.friend.repository;

import com.stella.board.friend.FriendRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRelationRepository
        extends JpaRepository<FriendRelation, Long> {

    // join fetch로 friend를 함께 가져오기 때문에 친구마다 User 조회 쿼리가 추가되는 N+1 문제를 피할 수 있다.
    @Query("""
            select relation
            from FriendRelation relation
            join fetch relation.friend friend
            where relation.owner.user_id = :ownerId
            order by relation.createdAt desc, relation.id desc
            """)
    List<FriendRelation> findAllWithFriendByOwnerId(
            @Param("ownerId") Long ownerId
    );

    @Query("""
            select case when count(relation) > 0 then true else false end
            from FriendRelation relation
            where relation.owner.user_id = :ownerId
              and relation.friend.user_id = :friendId
            """)
    boolean existsRelation(
            @Param("ownerId") Long ownerId,
            @Param("friendId") Long friendId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from FriendRelation relation
            where (relation.owner.user_id = :firstUserId
                   and relation.friend.user_id = :secondUserId)
               or (relation.owner.user_id = :secondUserId
                   and relation.friend.user_id = :firstUserId)
            """)
    int deleteBothDirections(
            @Param("firstUserId") Long firstUserId,
            @Param("secondUserId") Long secondUserId
    );
}

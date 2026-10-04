package com.stella.board.friend.repository;

import com.stella.board.friend.FriendApplying;
import com.stella.board.friend.RequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendApplyingRepository
        extends JpaRepository<FriendApplying, Long> {

    @Query("""
        select case when count(fa) > 0 then true else false end
        from FriendApplying fa
        where fa.status = :status
          and (
                (fa.sender.user_id = :firstUserId
                 and fa.receiver.user_id = :secondUserId)
             or (fa.sender.user_id = :secondUserId
                 and fa.receiver.user_id = :firstUserId)
          )
        """)
    boolean existsRequestBetween(
            @Param("firstUserId") Long firstUserId,
            @Param("secondUserId") Long secondUserId,
            @Param("status") RequestStatus status
    );

    @Query("""
        select fa
        from FriendApplying fa
        join fetch fa.sender sender
        where fa.receiver.user_id = :receiverId
          and fa.status = :status
        order by fa.requestedAt desc, fa.id desc
        """)
    List<FriendApplying> findReceivedRequests(
            @Param("receiverId") Long receiverId,
            @Param("status") RequestStatus status
    );

    @Query("""
        select fa
        from FriendApplying fa
        join fetch fa.receiver receiver
        where fa.sender.user_id = :senderId
          and fa.status = :status
        order by fa.requestedAt desc, fa.id desc
        """)
    List<FriendApplying> findSentRequests(
            @Param("senderId") Long senderId,
            @Param("status") RequestStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select fa
        from FriendApplying fa
        join fetch fa.sender
        join fetch fa.receiver
        where fa.id = :requestId
          and fa.receiver.user_id = :receiverId
        """)
    Optional<FriendApplying> findByIdAndReceiverIdForUpdate(
            @Param("requestId") Long requestId,
            @Param("receiverId") Long receiverId
    );
}

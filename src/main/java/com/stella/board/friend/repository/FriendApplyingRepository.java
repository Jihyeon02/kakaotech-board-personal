package com.stella.board.friend.repository;

import com.stella.board.friend.FriendApplying;
import com.stella.board.friend.RequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendApplyingRepository
        extends JpaRepository<FriendApplying, Long>,
        FriendApplyingQueryRepository {

    @Query("""
        select case when count(fa) > 0 then true else false end
        from FriendApplying fa
        where fa.sender.user_id = :senderId
          and fa.receiver.user_id = :receiverId
          and fa.status = :status
        """)
    boolean existsRequest(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("status") RequestStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select fa
        from FriendApplying fa
        join fetch fa.sender
        join fetch fa.receiver
        where fa.sender.user_id = :senderId
          and fa.receiver.user_id = :receiverId
          and fa.status = :status
        """)
    Optional<FriendApplying> findForUpdate(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("status") RequestStatus status
    );
}

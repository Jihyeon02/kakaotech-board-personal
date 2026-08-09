package com.stella.board.friend;

import com.stella.board.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "friend_applying",
        indexes = {
                @Index(
                        name = "idx_friend_applying_receiver_status_requested",
                        columnList = "receiver_id, status, requested_at"
                ),
                @Index(
                        name = "idx_friend_applying_sender_status_requested",
                        columnList = "sender_id, status, requested_at"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendApplying {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    private FriendApplying(
            User sender,
            User receiver,
            LocalDateTime requestedAt
    ) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestedAt = requestedAt;
        this.status = RequestStatus.WAITING;
    }

    public static FriendApplying create(
            User sender,
            User receiver
    ) {
        return new FriendApplying(
                sender,
                receiver,
                LocalDateTime.now()
        );
    }

    public void accept() {
        validateWaiting();
        this.status = RequestStatus.ACCEPTED;
    }

    public void reject() {
        validateWaiting();
        this.status = RequestStatus.REJECTED;
    }

    private void validateWaiting() {
        if (status != RequestStatus.WAITING) {
            throw new IllegalStateException(
                    "이미 처리된 친구 신청입니다."
            );
        }
    }

    public boolean isReceiver(Long userId) {
        return receiver.getUser_id().equals(userId);
    }
}

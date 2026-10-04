package com.stella.board.friend.dto;

import com.stella.board.friend.FriendApplying;
import com.stella.board.friend.RequestStatus;

import java.time.LocalDateTime;

public record FriendRequestCreatedResponse(
        Long requestId,
        Long senderId,
        Long receiverId,
        RequestStatus status,
        LocalDateTime requestedAt
) {

    public static FriendRequestCreatedResponse from(
            FriendApplying application
    ) {
        return new FriendRequestCreatedResponse(
                application.getId(),
                application.getSender().getUser_id(),
                application.getReceiver().getUser_id(),
                application.getStatus(),
                application.getRequestedAt()
        );
    }
}
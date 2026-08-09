package com.stella.board.friend.event;

public record FriendRequestRemovedEvent(
        Long receiverId,
        Long senderId
) {
}
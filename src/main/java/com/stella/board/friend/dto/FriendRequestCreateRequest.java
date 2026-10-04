package com.stella.board.friend.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequestCreateRequest(

        @NotNull
        Long receiverId

) {
}
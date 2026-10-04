package com.stella.board.friend.controller;

import com.stella.board.friend.dto.FriendRequestCreateRequest;
import com.stella.board.friend.dto.ReceivedFriendRequestResponse;
import com.stella.board.friend.dto.SentFriendRequestResponse;
import com.stella.board.friend.service.FriendRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    /**
     * 친구 신청
     */
    @PostMapping
    public ResponseEntity<Void> sendRequest(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId,

            @Valid @RequestBody
            FriendRequestCreateRequest request
    ) {
        Long requestId = friendRequestService.sendRequest(
                loginUserId,
                request.receiverId()
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/friend-requests/" + requestId
                        )
                )
                .build();
    }

    /**
     * 내가 받은 친구 신청 목록
     */
    @GetMapping("/received")
    public ResponseEntity<List<ReceivedFriendRequestResponse>>
    getReceivedRequests(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId
    ) {
        return ResponseEntity.ok(
                friendRequestService.getReceivedRequests(
                        loginUserId
                )
        );
    }

    /**
     * 내가 보낸 친구 신청 목록
     */
    @GetMapping("/sent")
    public ResponseEntity<List<SentFriendRequestResponse>>
    getSentRequests(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId
    ) {
        return ResponseEntity.ok(
                friendRequestService.getSentRequests(
                        loginUserId
                )
        );
    }

    /**
     * 친구 신청 수락
     */
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId,

            @PathVariable
            Long requestId
    ) {
        friendRequestService.acceptRequest(
                loginUserId,
                requestId
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * 친구 신청 거절
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId,

            @PathVariable
            Long requestId
    ) {
        friendRequestService.rejectRequest(
                loginUserId,
                requestId
        );

        return ResponseEntity.noContent().build();
    }
}

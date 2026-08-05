package com.stella.board.friend.controller;


import com.stella.board.friend.dto.FriendRequestCreateRequest;
import com.stella.board.friend.dto.FriendRequestCreatedResponse;
import com.stella.board.friend.dto.FriendRequestResponse;
import com.stella.board.friend.dto.FriendResponse;
import com.stella.board.friend.repository.FriendRelationQueryRepository;
import com.stella.board.friend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;


    @PostMapping("/requests") // 사실 받는 사람 ID로 바꿔야함 쿼리 파라미터
    public ResponseEntity<FriendRequestCreatedResponse>
    sendRequest(
            @RequestParam Long senderId,
            @Valid @RequestBody
            FriendRequestCreateRequest request
    ) {
        FriendRequestCreatedResponse response =
                friendService.sendRequest(
                        senderId,
                        request.receiverId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>>
    getReceivedRequests(
            @RequestParam Long receiverId
    ) {
        List<FriendRequestResponse> response =
                friendService.getReceivedRequests(
                        receiverId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/requests/{senderId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long senderId,
            @RequestParam Long receiverId
    ) {
        friendService.acceptRequest(
                receiverId,
                senderId
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/requests/{senderId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long senderId,
            @RequestParam Long receiverId
    ) {
        friendService.rejectRequest(
                receiverId,
                senderId
        );

        return ResponseEntity.noContent().build();
    }

    /*
     * 친구 목록 조회.
     *
     * GET /friend/list?userId=1
     */
    @GetMapping("/list")
    public ResponseEntity<List<FriendResponse>>
    getFriends(
            @RequestParam Long userId
    ) {
        List<FriendResponse> response =
                friendService.getFriends(userId);

        return ResponseEntity.ok(response);
    }
}

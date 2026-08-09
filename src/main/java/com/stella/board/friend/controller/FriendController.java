package com.stella.board.friend.controller;

import com.stella.board.friend.dto.FriendResponse;
import com.stella.board.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /**
     * 내 친구 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriendList(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId
    ) {
        return ResponseEntity.ok(
                friendService.getFriendList(loginUserId)
        );
    }

    /**
     * 친구 관계 삭제
     */
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> deleteFriend(
            @AuthenticationPrincipal(expression = "userId")
            Long loginUserId,

            @PathVariable
            Long friendUserId
    ) {
        friendService.deleteFriend(
                loginUserId,
                friendUserId
        );

        return ResponseEntity.noContent().build();
    }
}
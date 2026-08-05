package com.stella.board.friend;

import com.stella.board.friend.dto.FriendResponse;
import com.stella.board.friend.repository.FriendRelationRepository;
import com.stella.board.friend.service.FriendService;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class FriendRequestServiceIntegrationTest {

    @Autowired
    private FriendService friendRequestService;

    @Autowired
    private FriendRelationRepository relationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 친구_신청을_수락하면_양방향_친구관계가_생성된다() {
        // given
        User sender = userRepository.save(
                new User(
                        444L,
                        "friend-sender@test.com",
                        "password123",
                        "sender",
                        null
                )
        );

        User receiver = userRepository.save(
                new User(
                        454L,
                        "friend-receiver@test.com",
                        "password123",
                        "receiver",
                        null
                )
        );

        Long senderId = sender.getUser_id();
        Long receiverId = receiver.getUser_id();

        // when: 친구 신청
        friendRequestService.sendRequest(
                senderId,
                receiverId
        );

        // when: 친구 신청 수락
        friendRequestService.acceptRequest(
                receiverId,
                senderId
        );

        // then: 양방향 관계 확인
        assertTrue(
                relationRepository.existsRelation(
                        senderId,
                        receiverId
                )
        );

        assertTrue(
                relationRepository.existsRelation(
                        receiverId,
                        senderId
                )
        );

        // then: sender의 친구 목록
        List<FriendResponse> senderFriends =
                friendRequestService.getFriends(senderId);

        assertEquals(1, senderFriends.size());
        assertEquals(
                receiverId,
                senderFriends.getFirst().friendId()
        );

        // then: receiver의 친구 목록
        List<FriendResponse> receiverFriends =
                friendRequestService.getFriends(receiverId);

        assertEquals(1, receiverFriends.size());
        assertEquals(
                senderId,
                receiverFriends.getFirst().friendId()
        );
    }
}
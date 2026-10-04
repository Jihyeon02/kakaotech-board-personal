package com.stella.board.friend.service;

import com.stella.board.friend.FriendApplying;
import com.stella.board.friend.FriendRelation;
import com.stella.board.friend.RequestStatus;
import com.stella.board.friend.dto.ReceivedFriendRequestResponse;
import com.stella.board.friend.dto.SentFriendRequestResponse;
import com.stella.board.friend.repository.FriendApplyingRepository;
import com.stella.board.friend.repository.FriendRelationRepository;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final UserRepository userRepository;
    private final FriendApplyingRepository applyingRepository;
    private final FriendRelationRepository relationRepository;

    @Transactional
    public Long sendRequest(
            Long senderId,
            Long receiverId
    ) {
        validateDifferentUsers(senderId, receiverId);

        User sender = findUser(senderId, "신청자");
        User receiver = findUser(receiverId, "수신자");

        if (relationRepository.existsRelation(senderId, receiverId)) {
            throw new IllegalStateException(
                    "이미 친구 관계입니다."
            );
        }

        if (applyingRepository.existsRequestBetween(
                senderId,
                receiverId,
                RequestStatus.WAITING
        )) {
            throw new IllegalStateException(
                    "두 사용자 사이에 이미 대기 중인 친구 신청이 있습니다."
            );
        }

        FriendApplying savedRequest = applyingRepository.save(
                FriendApplying.create(sender, receiver)
        );

        return savedRequest.getId();
    }

    @Transactional(readOnly = true)
    public List<ReceivedFriendRequestResponse> getReceivedRequests(
            Long receiverId
    ) {
        return applyingRepository
                .findReceivedRequests(
                        receiverId,
                        RequestStatus.WAITING
                )
                .stream()
                .map(request ->
                        new ReceivedFriendRequestResponse(
                                request.getId(),
                                request.getSender().getUser_id(),
                                request.getSender().getNickname(),
                                request.getRequestedAt()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SentFriendRequestResponse> getSentRequests(
            Long senderId
    ) {
        return applyingRepository
                .findSentRequests(
                        senderId,
                        RequestStatus.WAITING
                )
                .stream()
                .map(request ->
                        new SentFriendRequestResponse(
                                request.getId(),
                                request.getReceiver().getUser_id(),
                                request.getReceiver().getNickname(),
                                request.getRequestedAt()
                        )
                )
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(
                    cacheNames = FriendService.FRIEND_LIST_CACHE,
                    key = "#receiverId"
            ),
            @CacheEvict(
                    cacheNames = FriendService.FRIEND_LIST_CACHE,
                    key = "#result"
            )
    })
    @Transactional
    public Long acceptRequest(
            Long receiverId,
            Long requestId
    ) {
        FriendApplying request = findRequestForUpdate(
                requestId,
                receiverId
        );

        request.accept();

        User sender = request.getSender();
        User receiver = request.getReceiver();

        if (relationRepository.existsRelation(
                sender.getUser_id(),
                receiver.getUser_id()
        )) {
            throw new IllegalStateException(
                    "이미 친구 관계입니다."
            );
        }

        LocalDateTime relatedAt = LocalDateTime.now();

        relationRepository.saveAll(
                List.of(
                        FriendRelation.create(
                                sender,
                                receiver,
                                relatedAt
                        ),
                        FriendRelation.create(
                                receiver,
                                sender,
                                relatedAt
                        )
                )
        );

        return sender.getUser_id();
    }

    @Transactional
    public void rejectRequest(
            Long receiverId,
            Long requestId
    ) {
        FriendApplying request = findRequestForUpdate(
                requestId,
                receiverId
        );

        request.reject();
    }

    private FriendApplying findRequestForUpdate(
            Long requestId,
            Long receiverId
    ) {
        return applyingRepository
                .findByIdAndReceiverIdForUpdate(
                        requestId,
                        receiverId
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "처리할 수 있는 친구 신청을 찾을 수 없습니다."
                        )
                );
    }

    private User findUser(
            Long userId,
            String role
    ) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                role + "를 찾을 수 없습니다."
                        )
                );
    }

    private void validateDifferentUsers(
            Long senderId,
            Long receiverId
    ) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException(
                    "자기 자신에게 친구 신청할 수 없습니다."
            );
        }
    }
}

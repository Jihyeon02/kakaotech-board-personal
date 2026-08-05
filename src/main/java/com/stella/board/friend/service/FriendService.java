package com.stella.board.friend.service;

import com.stella.board.friend.FriendApplying;
import com.stella.board.friend.FriendRelation;
import com.stella.board.friend.RequestStatus;
import com.stella.board.friend.dto.FriendRequestCacheRow;
import com.stella.board.friend.dto.FriendRequestCreatedResponse;
import com.stella.board.friend.dto.FriendRequestResponse;
import com.stella.board.friend.dto.FriendResponse;
import com.stella.board.friend.event.FriendRequestCreatedEvent;
import com.stella.board.friend.event.FriendRequestRemovedEvent;
import com.stella.board.friend.repository.FriendApplyingRepository;
import com.stella.board.friend.repository.FriendRelationRepository;
import com.stella.board.friend.repository.FriendRequestRedisRepository;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendApplyingRepository applyingRepository;
    private final FriendRelationRepository relationRepository;
    private final FriendRequestRedisRepository redisRepository;
    private final ApplicationEventPublisher eventPublisher;

    /*
     * 친구 신청 전송.
     */
    @Transactional
    public FriendRequestCreatedResponse sendRequest(
            Long senderId,
            Long receiverId
    ) {
        validateDifferentUsers(senderId, receiverId);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "신청자를 찾을 수 없습니다."
                        )
                );

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "수신자를 찾을 수 없습니다."
                        )
                );

        boolean alreadyWaiting =
                applyingRepository.existsRequest(
                        senderId,
                        receiverId,
                        RequestStatus.WAITING
                );

        if (alreadyWaiting) {
            throw new IllegalStateException(
                    "이미 보낸 친구 신청입니다."
            );
        }

        boolean reverseRequestExists =
                applyingRepository.existsRequest(
                        receiverId,
                        senderId,
                        RequestStatus.WAITING
                );

        if (reverseRequestExists) {
            throw new IllegalStateException(
                    "상대방이 이미 친구 신청을 보냈습니다."
            );
        }

        if (relationRepository.existsRelation(
                senderId,
                receiverId
        )) {
            throw new IllegalStateException(
                    "이미 친구 관계입니다."
            );
        }

        FriendApplying application =
                FriendApplying.create(
                        sender,
                        receiver
                );

        FriendApplying savedApplication =
                applyingRepository.save(application);

        eventPublisher.publishEvent(
                new FriendRequestCreatedEvent(
                        receiverId,
                        senderId,
                        savedApplication.getRequestedAt()
                )
        );

        return FriendRequestCreatedResponse.from(
                savedApplication
        );
    }

    /*
     * 받은 친구 신청 목록.
     *
     * Read-Through:
     * Redis가 로딩됐다면 Redis 반환.
     * 아니면 QueryDSL로 DB를 읽고 Redis에 저장.
     */
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedRequests(
            Long receiverId
    ) {
        if (redisRepository.isLoaded(receiverId)) {
            return redisRepository.findAll(receiverId);
        }

        List<FriendRequestCacheRow> dbRows =
                applyingRepository
                        .findWaitingRequestsByReceiverId(
                                receiverId
                        );

        redisRepository.replaceAll(
                receiverId,
                dbRows
        );



        return dbRows.stream()
                .map(row ->
                        new FriendRequestResponse(
                                row.senderId(),
                                row.requestedAt()
                        )
                )
                .toList();
    }

    /*
     * 친구 신청 수락.
     */
    @Transactional
    public void acceptRequest(
            Long receiverId,
            Long senderId
    ) {
        FriendApplying application =
                applyingRepository.findForUpdate(
                                senderId,
                                receiverId,
                                RequestStatus.WAITING
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "대기 중인 친구 신청이 없습니다."
                                )
                        );

        if (!application.isReceiver(receiverId)) {
            throw new IllegalStateException(
                    "친구 신청을 수락할 권한이 없습니다."
            );
        }

        application.accept();

        User sender = application.getSender();
        User receiver = application.getReceiver();

        LocalDateTime createdAt = LocalDateTime.now();

        // 양방향 저장 -> sender, receiver 모두 저장
        List<FriendRelation> relations = List.of(
                FriendRelation.create(
                        sender,
                        receiver,
                        createdAt
                ),
                FriendRelation.create(
                        receiver,
                        sender,
                        createdAt
                )
        );

        relationRepository.saveAll(relations);

        // FriendApplying애서 해당 컬럼 삭제


        eventPublisher.publishEvent(
                new FriendRequestRemovedEvent(
                        receiverId,
                        senderId
                )
        );
    }

    /*
     * 친구 신청 거절.
     */
    @Transactional
    public void rejectRequest(
            Long receiverId,
            Long senderId
    ) {
        FriendApplying application =
                applyingRepository.findForUpdate(
                                senderId,
                                receiverId,
                                RequestStatus.WAITING
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "대기 중인 친구 신청이 없습니다."
                                )
                        );

        if (!application.isReceiver(receiverId)) {
            throw new IllegalStateException(
                    "친구 신청을 거절할 권한이 없습니다."
            );
        }

        application.reject();

        eventPublisher.publishEvent(
                new FriendRequestRemovedEvent(
                        receiverId,
                        senderId
                )
        );
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return relationRepository.findFriends(userId);
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

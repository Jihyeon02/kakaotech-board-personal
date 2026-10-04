package com.stella.board.friend.service;

import com.stella.board.friend.dto.FriendResponse;
import com.stella.board.friend.FriendRelation;
import com.stella.board.friend.repository.FriendRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FriendService {

    public static final String FRIEND_LIST_CACHE = "friendList";

    private final FriendRelationRepository friendRelationRepository;

    @Cacheable(
            cacheNames = FRIEND_LIST_CACHE,
            key = "#ownerId"
    )
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriendList(Long ownerId) {
        return friendRelationRepository
                .findAllWithFriendByOwnerId(ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(
                    cacheNames = FRIEND_LIST_CACHE,
                    key = "#ownerId"
            ),
            @CacheEvict(
                    cacheNames = FRIEND_LIST_CACHE,
                    key = "#friendUserId"
            )
    })
    @Transactional
    public void deleteFriend(
            Long ownerId,
            Long friendUserId
    ) {
        if (ownerId.equals(friendUserId)) {
            throw new IllegalArgumentException(
                    "자기 자신은 친구 목록에서 삭제할 수 없습니다."
            );
        }

        int deletedCount = friendRelationRepository
                .deleteBothDirections(ownerId, friendUserId);

        if (deletedCount == 0) {
            throw new NoSuchElementException(
                    "친구 관계를 찾을 수 없습니다."
            );
        }
    }

    private FriendResponse toResponse(FriendRelation relation) {
        return new FriendResponse(
                relation.getFriend().getUser_id(),
                relation.getFriend().getNickname(),
                relation.getFriend().getProfile_imageUrl(),
                relation.getCreatedAt()
        );
    }
}

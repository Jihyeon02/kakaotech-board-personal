package com.stella.board.friend.event;

import com.stella.board.friend.repository.FriendRequestRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FriendRequestCacheEventListener {

    private final FriendRequestRedisRepository redisRepository;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleCreated(
            FriendRequestCreatedEvent event
    ) {
        redisRepository.addIfLoaded(
                event.receiverId(),
                event.senderId(),
                event.requestedAt()
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleRemoved(
            FriendRequestRemovedEvent event
    ) {
        redisRepository.remove(
                event.receiverId(),
                event.senderId()
        );
    }
}
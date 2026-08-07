package com.stella.board.friend.repository;

import com.stella.board.friend.dto.FriendRequestCacheRow;
import com.stella.board.friend.dto.FriendRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FriendRequestRedisRepository {

    private static final String KEY_PREFIX =
            "friend:request:received:";

    private static final Duration TTL =
            Duration.ofMinutes(10);

    private static final ZoneId APPLICATION_ZONE =
            ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate redisTemplate;

    /*
     * Redis에 전체 DB 결과가 한 번 적재됐는지 확인한다.
     */
    public boolean isLoaded(Long receiverId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(loadedKey(receiverId))
        );
    }

    /*
     * Redis Sorted Set 최신순 조회.
     */
    public List<FriendRequestResponse> findAll(
            Long receiverId
    ) {
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(
                                dataKey(receiverId),
                                0,
                                -1
                        );

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .filter(tuple ->
                        tuple.getValue() != null
                                && tuple.getScore() != null
                )
                .map(tuple ->
                        new FriendRequestResponse(
                                Long.valueOf(
                                        tuple.getValue()
                                ),
                                fromScore(
                                        tuple.getScore()
                                )
                        )
                )
                .toList();
    }

    /*
     * Cache miss 때 DB 전체 조회 결과로 캐시를 교체한다.
     */
    public void replaceAll(
            Long receiverId,
            List<FriendRequestCacheRow> requests
    ) {
        String dataKey = dataKey(receiverId);
        String loadedKey = loadedKey(receiverId);

        redisTemplate.delete(dataKey);

        if (!requests.isEmpty()) {
            Set<ZSetOperations.TypedTuple<String>> tuples =
                    requests.stream()
                            .map(request ->
                                    ZSetOperations.TypedTuple.of(
                                            request.senderId()
                                                    .toString(),
                                            toScore(
                                                    request.requestedAt()
                                            )
                                    )
                            )
                            .collect(Collectors.toSet());

            redisTemplate.opsForZSet()
                    .add(dataKey, tuples);

            redisTemplate.expire(dataKey, TTL);
        }

        /*
         * DB 결과가 빈 목록이어도 로딩됐다는 사실은 캐싱한다.
         */
        redisTemplate.opsForValue()
                .set(loadedKey, "1", TTL);
    }

    /*
     * 친구 신청 생성 시 Write-Through 갱신.
     *
     * 중요한 부분:
     * 캐시가 아직 로딩되지 않았다면 ZADD하지 않는다.
     *
     * 로딩 전 ZADD하면 방금 생성된 신청 하나만 들어 있는
     * 불완전한 캐시가 만들어질 수 있다.
     */
    public void addIfLoaded(
            Long receiverId,
            Long senderId,
            LocalDateTime requestedAt
    ) {
        if (!isLoaded(receiverId)) {
            return;
        }

        redisTemplate.opsForZSet()
                .add(
                        dataKey(receiverId),
                        senderId.toString(),
                        toScore(requestedAt)
                );

        refreshTtl(receiverId);
    }

    /*
     * 수락 또는 거절 시 해당 신청 제거.
     */
    public void remove(
            Long receiverId,
            Long senderId
    ) {
        redisTemplate.opsForZSet()
                .remove(
                        dataKey(receiverId),
                        senderId.toString()
                );

        if (isLoaded(receiverId)) {
            refreshTtl(receiverId);
        }
    }

    /*
     * 캐시 전체 삭제가 필요한 경우.
     */
    public void evict(Long receiverId) {
        redisTemplate.delete(
                List.of(
                        dataKey(receiverId),
                        loadedKey(receiverId)
                )
        );
    }

    private void refreshTtl(Long receiverId) {
        redisTemplate.expire(
                dataKey(receiverId),
                TTL
        );

        redisTemplate.expire(
                loadedKey(receiverId),
                TTL
        );
    }

    private double toScore(LocalDateTime requestedAt) {
        return requestedAt
                .atZone(APPLICATION_ZONE)
                .toInstant()
                .toEpochMilli();
    }

    private LocalDateTime fromScore(Double score) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(score.longValue()),
                APPLICATION_ZONE
        );
    }

    private String dataKey(Long receiverId) {
        return KEY_PREFIX + receiverId;
    }

    private String loadedKey(Long receiverId) {
        return KEY_PREFIX + receiverId + ":loaded";
    }
}
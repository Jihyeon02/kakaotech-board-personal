package com.stella.board.friend;

import com.stella.board.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "friend_relation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_friend_relation_owner_friend",
                columnNames = {
                        "owner_id",
                        "friend_id"
                }
        ),
        indexes = @Index(
                name = "idx_friend_relation_owner_created",
                columnList = "owner_id, created_at"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 친구 목록의 주인.
     *
     * 예: owner=1, friend=5
     * → 1번 사용자의 친구 목록에 5번이 존재한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /*
     * owner 사용자의 친구.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private FriendRelation(
            User owner,
            User friend,
            LocalDateTime createdAt
    ) {
        this.owner = owner;
        this.friend = friend;
        this.createdAt = createdAt;
    }
    public static FriendRelation create(
            User owner,
            User friend,
            LocalDateTime createdAt
    ) {
        if (owner.getUser_id().equals(friend.getUser_id())) {
            throw new IllegalArgumentException(
                    "자기 자신과 친구 관계를 만들 수 없습니다."
            );
        }

        return new FriendRelation(
                owner,
                friend,
                createdAt
        );
    }
}

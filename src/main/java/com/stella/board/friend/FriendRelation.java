package com.stella.board.friend;

import com.stella.board.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
                name = "uk_friend_relation_users",
                columnNames = {"user_a_id", "user_b_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private FriendRelation(User userA, User userB) {
        this.userA = userA;
        this.userB = userB;
        this.createdAt = LocalDateTime.now();
    }

    public static FriendRelation create(User firstUser, User secondUser) {
        Long firstUserId = firstUser.getUser_id();
        Long secondUserId = secondUser.getUser_id();

        if (firstUserId.equals(secondUserId)) {
            throw new IllegalArgumentException(
                    "자기 자신과 친구 관계를 만들 수 없습니다."
            );
        }

        if (firstUserId.compareTo(secondUserId) < 0) {
            return new FriendRelation(firstUser, secondUser);
        }

        return new FriendRelation(secondUser, firstUser);
    }
}

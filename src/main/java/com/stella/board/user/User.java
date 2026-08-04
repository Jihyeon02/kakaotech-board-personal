package com.stella.board.user;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 매번 저 변수들 바뀌거나 생성될때마다  바로 DB에 쿼리를 쏴서 데이터가 바껴야하는데 이게 성능관점에서 ㄱㅊ을지 고민해봐야할 것 같음.


@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    // api 설계 대로 private로 필드 선언 -> 사진 관련된 설계는 뒤로 빼서 나중에 넣을 예정
    @NotNull @Column(unique = true)
    private String email;

    @Size(min = 8, max = 20) @NotNull
    private String password;

    @Size(max = 10) @NotNull @Column(unique = true)
    private String nickname;

    @Nullable
    @Column(name = "profile_image_url")
    private String profile_imageUrl;


    // 비즈니스 전용 생성자
    public User (Long user_id, String email, String password, String nickname, String profile_imageUrl) {
        this.user_id = user_id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_imageUrl = profile_imageUrl;
    }




}

package com.stella.board.user;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Entity(name = "users")
@Getter
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
    public User (String email, String password, String nickname, String profile_imageUrl) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_imageUrl = profile_imageUrl;
    }



}

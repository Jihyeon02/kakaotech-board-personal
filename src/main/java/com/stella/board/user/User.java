//package com.stella.board.user;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Getter @Setter
//public class User {
//    @Id
//    private Long id;
//
//    // api 설계 대로 private로 필드 선언 -> 사진 관련된 설계는 뒤로 빼서 나중에 넣을 예정
//    @NotNull
//    private String email;
//    @Size(min = 8, max = 20) @NotNull
//    private String password;
//    @Size(max = 10) @NotNull
//    private String nickname;
//
//
//    // 비즈니스 전용 생성자
//    public User (String email, String password, String nickname) {
//        this.email = email;
//        this.password = password;
//        this.nickname = nickname;
//    }
//
//
//
//}

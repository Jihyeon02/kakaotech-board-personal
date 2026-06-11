package com.stella.board.user;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // AuthService파일에서 쓰이는 메소드
    Optional<User> findByEmail(String email);

    @Override
    void deleteById(Long id); // 회원정보 삭제

    @Override
    <S extends User> S save(S entity); // user 정보 저장 - 회원 가입

    @Override
    Optional<User> findById(Long id); // user 정보 단건 조회 -> 수정 시 사용

}

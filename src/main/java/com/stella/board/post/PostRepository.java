package com.stella.board.post;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    Optional<Post> findById(Long post_id);

    // 전체 조회용 메서드
    List<Post> findAll();

    @Override
    <S extends Post> S save(S entity);
}

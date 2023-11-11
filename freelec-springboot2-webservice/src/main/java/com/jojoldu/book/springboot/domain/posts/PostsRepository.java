package com.jojoldu.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * interface로 생성, JpaRepository<Entity 클래스, PK타입> 상속 시 기본적인 CRUD 메소드 자동 생성
 */
public interface PostsRepository extends JpaRepository<Posts, Long> {
}

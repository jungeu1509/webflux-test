package com.example.demo.webflux.repository;

import com.example.demo.webflux.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive Repository 인터페이스
 *
 * 실제 프로젝트에서는:
 * - Spring Data R2DBC: 관계형 데이터베이스용 Reactive Repository
 * - Spring Data Reactive MongoDB: MongoDB용 Reactive Repository
 * - ReactiveCrudRepository를 extends하여 기본 CRUD 메서드 상속
 *
 * 예시:
 * public interface UserRepository extends ReactiveCrudRepository<User, Long> {
 *     Flux<User> findByAge(Integer age);
 *     Flux<User> findByNameContaining(String name);
 * }
 */
public interface UserRepository {

    /**
     * 모든 사용자 조회
     * @return Flux<User>
     */
    Flux<User> findAll();

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return Mono<User>
     */
    Mono<User> findById(Long id);

    /**
     * 사용자 저장/업데이트
     * @param user 사용자 객체
     * @return Mono<User>
     */
    Mono<User> save(User user);

    /**
     * 사용자 삭제
     * @param id 사용자 ID
     * @return Mono<Void>
     */
    Mono<Void> deleteById(Long id);

    /**
     * 나이로 사용자 검색
     * @param age 나이
     * @return Flux<User>
     */
    Flux<User> findByAge(Integer age);

    /**
     * 이름으로 사용자 검색 (부분 일치)
     * @param name 이름
     * @return Flux<User>
     */
    Flux<User> findByNameContaining(String name);

    /**
     * 사용자 수 조회
     * @return Mono<Long>
     */
    Mono<Long> count();

    /**
     * 모든 사용자 삭제
     * @return Mono<Void>
     */
    Mono<Void> deleteAll();
}

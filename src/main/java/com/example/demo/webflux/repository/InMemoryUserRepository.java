package com.example.demo.webflux.repository;

import com.example.demo.webflux.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 메모리 기반 UserRepository 구현체
 *
 * 학습 목적의 간단한 구현
 * 실제 프로젝트에서는 Spring Data R2DBC 또는 Reactive MongoDB 사용
 *
 * 주요 개념:
 * 1. ConcurrentHashMap: 동시성 문제 해결
 * 2. Mono.defer(): 구독 시점에 실행 (Lazy evaluation)
 * 3. Flux.fromIterable(): 컬렉션을 Flux로 변환
 * 4. switchIfEmpty(): 결과가 없을 때 대체 값 제공
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public InMemoryUserRepository() {
        // 초기 데이터 생성
        initializeData();
    }

    private void initializeData() {
        save(new User(null, "홍길동", "hong@example.com", 25)).subscribe();
        save(new User(null, "김철수", "kim@example.com", 30)).subscribe();
        save(new User(null, "이영희", "lee@example.com", 28)).subscribe();
        save(new User(null, "박민수", "park@example.com", 35)).subscribe();
        save(new User(null, "최지영", "choi@example.com", 22)).subscribe();
    }

    @Override
    public Flux<User> findAll() {
        // Mono.defer를 사용하여 구독 시점에 실행
        return Flux.defer(() -> Flux.fromIterable(users.values()));
    }

    @Override
    public Mono<User> findById(Long id) {
        return Mono.defer(() -> {
            User user = users.get(id);
            return user != null ? Mono.just(user) : Mono.empty();
        });
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.defer(() -> {
            if (user.getId() == null) {
                // 새로운 사용자 생성
                user.setId(idGenerator.getAndIncrement());
            }
            users.put(user.getId(), user);
            return Mono.just(user);
        });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.defer(() -> {
            users.remove(id);
            return Mono.empty();
        });
    }

    /**
     * ID로 사용자 존재 여부 확인
     * @param id 사용자 ID
     * @return Mono<Boolean>
     */
    public Mono<Boolean> existsById(Long id) {
        return Mono.defer(() -> Mono.just(users.containsKey(id)));
    }

    @Override
    public Flux<User> findByAge(Integer age) {
        return findAll()
            .filter(user -> user.getAge() != null && user.getAge().equals(age));
    }

    @Override
    public Flux<User> findByNameContaining(String name) {
        return findAll()
            .filter(user -> user.getName() != null && user.getName().contains(name));
    }

    @Override
    public Mono<Long> count() {
        return Mono.defer(() -> Mono.just((long) users.size()));
    }

    @Override
    public Mono<Void> deleteAll() {
        return Mono.defer(() -> {
            users.clear();
            return Mono.empty();
        });
    }
}

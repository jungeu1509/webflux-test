package com.example.demo.webflux.service;

import com.example.demo.webflux.model.User;
import com.example.demo.webflux.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * UserService
 *
 * 비즈니스 로직을 처리하는 서비스 레이어
 * Reactive Repository와 통신하여 데이터를 처리
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 모든 사용자 조회
     */
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * ID로 사용자 조회
     */
    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 사용자 저장
     */
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    /**
     * 사용자 업데이트
     */
    public Mono<User> update(User user) {
        return userRepository.findById(user.getId())
            .flatMap(existingUser -> {
                existingUser.setName(user.getName());
                existingUser.setEmail(user.getEmail());
                existingUser.setAge(user.getAge());
                return userRepository.save(existingUser);
            });
    }

    /**
     * 사용자 삭제
     */
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    /**
     * 나이로 사용자 검색
     */
    public Flux<User> findByAge(Integer age) {
        return userRepository.findByAge(age);
    }

    /**
     * 이름으로 사용자 검색
     */
    public Flux<User> findByNameContaining(String name) {
        return userRepository.findByNameContaining(name);
    }

    /**
     * 사용자 수 조회
     */
    public Mono<Long> count() {
        return userRepository.count();
    }

    /**
     * 비즈니스 로직 예제: 성인 사용자만 필터링
     */
    public Flux<User> findAdults() {
        return userRepository.findAll()
            .filter(user -> user.getAge() >= 18);
    }

    /**
     * 비즈니스 로직 예제: 사용자 이름을 대문자로 변환하여 저장
     */
    public Mono<User> saveWithUppercaseName(User user) {
        if (user.getName() != null) {
            user.setName(user.getName().toUpperCase());
        }
        return userRepository.save(user);
    }
}

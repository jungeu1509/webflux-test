package com.example.demo.webflux.controller;

import com.example.demo.webflux.model.User;
import com.example.demo.webflux.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * WebFlux Controller 예제
 *
 * 주요 개념:
 * 1. @RestController: RESTful API 컨트롤러
 * 2. Mono<T>: 0..1 개의 응답 (단일 객체)
 * 3. Flux<T>: 0..N 개의 응답 (여러 객체)
 * 4. @RequestBody: 요청 본문을 Reactive 타입으로 받기
 * 5. ResponseEntity: HTTP 상태 코드와 함께 응답
 *
 * 기존 Spring MVC와의 차이점:
 * - 반환 타입이 Mono<T> 또는 Flux<T>
 * - 논블로킹 방식으로 동작
 * - 스레드를 블로킹하지 않고 이벤트 기반으로 처리
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1. 모든 사용자 조회 (Flux)
     * GET /api/users
     *
     * Flux를 사용하여 여러 User 객체를 스트림으로 반환
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.findAll();
    }

    /**
     * 2. 특정 사용자 조회 (Mono)
     * GET /api/users/{id}
     *
     * Mono를 사용하여 단일 User 객체 반환
     * 존재하지 않으면 404 응답
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(user -> ResponseEntity.ok(user))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 3. 사용자 생성 (Mono)
     * POST /api/users
     *
     * 요청 본문을 Mono<User>로 받아서 처리
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody Mono<User> userMono) {
        return userMono.flatMap(userService::save);
    }

    /**
     * 4. 사용자 수정 (Mono)
     * PUT /api/users/{id}
     *
     * 기존 사용자 정보를 업데이트
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable Long id,
            @RequestBody Mono<User> userMono) {

        return userMono.flatMap(user -> {
            user.setId(id);
            return userService.update(user);
        })
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 5. 사용자 삭제 (Mono)
     * DELETE /api/users/{id}
     *
     * 먼저 사용자 존재 여부를 확인한 후 삭제
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return userService.findById(id)
            .flatMap(user -> userService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 6. 나이별 사용자 검색 (Flux)
     * GET /api/users/age/{age}
     */
    @GetMapping("/age/{age}")
    public Flux<User> getUsersByAge(@PathVariable Integer age) {
        return userService.findByAge(age);
    }

    /**
     * 7. 이름으로 검색 (Flux)
     * GET /api/users/search?name={name}
     */
    @GetMapping("/search")
    public Flux<User> searchUsersByName(@RequestParam String name) {
        return userService.findByNameContaining(name);
    }

    /**
     * 8. Server-Sent Events (SSE) - 실시간 스트리밍
     * GET /api/users/stream
     *
     * produces = MediaType.TEXT_EVENT_STREAM_VALUE
     * - 클라이언트에게 실시간으로 데이터를 푸시
     * - 1초마다 사용자 정보를 전송
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamUsers() {
        return userService.findAll()
            .delayElements(Duration.ofSeconds(1));  // 1초 간격으로 전송
    }

    /**
     * 9. 배치 생성 (Flux)
     * POST /api/users/batch
     *
     * 여러 사용자를 한번에 생성
     */
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<User> createUsers(@RequestBody Flux<User> usersFlux) {
        return usersFlux.flatMap(userService::save);
    }

    /**
     * 10. 사용자 수 조회 (Mono)
     * GET /api/users/count
     */
    @GetMapping("/count")
    public Mono<Long> getUserCount() {
        return userService.count();
    }

    /**
     * 11. 에러 처리 예제
     * GET /api/users/error-example
     *
     * onErrorResume을 사용한 에러 처리
     */
    @GetMapping("/error-example")
    public Mono<ResponseEntity<String>> errorExample() {
        return Mono.error(new RuntimeException("의도적인 에러"))
            .cast(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(error -> {
                // 에러 발생 시 대체 응답 반환
                return Mono.just(
                    ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("에러 발생: " + error.getMessage())
                );
            });
    }

    /**
     * 12. 타임아웃 예제
     * GET /api/users/timeout-example
     *
     * timeout을 사용한 시간 제한
     */
    @GetMapping("/timeout-example")
    public Mono<ResponseEntity<String>> timeoutExample() {
        return Mono.delay(Duration.ofSeconds(5))
            .map(tick -> "완료!")
            .timeout(Duration.ofSeconds(2))
            .map(ResponseEntity::ok)
            .onErrorReturn(
                ResponseEntity
                    .status(HttpStatus.REQUEST_TIMEOUT)
                    .body("타임아웃 발생!")
            );
    }

    /**
     * 13. Zip 연산 예제
     * GET /api/users/zip-example
     *
     * 여러 Mono를 조합하여 응답
     */
    @GetMapping("/zip-example")
    public Mono<ResponseEntity<String>> zipExample() {
        Mono<Long> countMono = userService.count();
        Mono<User> firstUserMono = userService.findAll().next();

        return Mono.zip(countMono, firstUserMono)
            .map(tuple -> ResponseEntity.ok(
                "전체 사용자: " + tuple.getT1() + ", " +
                "첫 번째 사용자: " + tuple.getT2().getName()
            ))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

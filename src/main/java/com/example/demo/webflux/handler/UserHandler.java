package com.example.demo.webflux.handler;

import com.example.demo.webflux.model.User;
import com.example.demo.webflux.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Handler - Functional Endpoints용 핸들러
 *
 * Router Functions에서 사용하는 비즈니스 로직 처리
 * Controller 대신 Handler + Router 조합으로 엔드포인트 정의
 *
 * 주요 개념:
 * 1. ServerRequest: HTTP 요청 정보
 * 2. ServerResponse: HTTP 응답 생성
 * 3. 함수형 프로그래밍 스타일
 */
@Component
public class UserHandler {

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * 모든 사용자 조회
     * GET /functional/users
     */
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userService.findAll(), User.class);
    }

    /**
     * ID로 사용자 조회
     * GET /functional/users/{id}
     */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        return parseId(request.pathVariable("id"))
            .flatMap(id -> userService.findById(id)
                .flatMap(user ->
                    ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                )
                .switchIfEmpty(ServerResponse.notFound().build())
            )
            .onErrorResume(NumberFormatException.class, e ->
                ServerResponse.badRequest().bodyValue("Invalid ID format")
            );
    }

    /**
     * 사용자 생성
     * POST /functional/users
     */
    public Mono<ServerResponse> createUser(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);

        return userMono
            .flatMap(userService::save)
            .flatMap(savedUser ->
                ServerResponse
                    .created(URI.create("/functional/users/" + savedUser.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(savedUser)
            );
    }

    /**
     * 사용자 수정
     * PUT /functional/users/{id}
     */
    public Mono<ServerResponse> updateUser(ServerRequest request) {
        return parseId(request.pathVariable("id"))
            .flatMap(id -> {
                Mono<User> userMono = request.bodyToMono(User.class);
                return userMono
                    .flatMap(user -> {
                        user.setId(id);
                        return userService.update(user);
                    })
                    .flatMap(updatedUser ->
                        ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(updatedUser)
                    )
                    .switchIfEmpty(ServerResponse.notFound().build());
            })
            .onErrorResume(NumberFormatException.class, e ->
                ServerResponse.badRequest().bodyValue("Invalid ID format")
            );
    }

    /**
     * 사용자 삭제
     * DELETE /functional/users/{id}
     *
     * 먼저 사용자 존재 여부를 확인한 후 삭제
     */
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        return parseId(request.pathVariable("id"))
            .flatMap(id -> userService.findById(id)
                .flatMap(user -> userService.deleteById(id)
                    .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build())
            )
            .onErrorResume(NumberFormatException.class, e ->
                ServerResponse.badRequest().bodyValue("Invalid ID format")
            );
    }

    /**
     * 문자열을 Long으로 파싱하는 헬퍼 메서드
     */
    private Mono<Long> parseId(String id) {
        return Mono.fromCallable(() -> Long.valueOf(id));
    }

    /**
     * 사용자 수 조회
     * GET /functional/users/count
     */
    public Mono<ServerResponse> getUserCount(ServerRequest request) {
        return userService.count()
            .flatMap(count ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{ \"count\": " + count + " }")
            );
    }

    /**
     * 이름으로 검색
     * GET /functional/users/search?name={name}
     */
    public Mono<ServerResponse> searchUsers(ServerRequest request) {
        String name = request.queryParam("name").orElse("");

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userService.findByNameContaining(name), User.class);
    }

    /**
     * Server-Sent Events 스트리밍
     * GET /functional/users/stream
     */
    public Mono<ServerResponse> streamUsers(ServerRequest request) {
        return ServerResponse
            .ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(userService.findAll(), User.class);
    }
}

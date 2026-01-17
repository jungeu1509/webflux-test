package com.example.demo.webflux.config;

import com.example.demo.webflux.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Router Functions 설정
 *
 * 함수형 엔드포인트(Functional Endpoints) 정의
 *
 * Controller vs Router Functions:
 * - Controller: 어노테이션 기반, 객체 지향적
 * - Router Functions: 함수형 프로그래밍, 더 유연하고 테스트하기 쉬움
 *
 * 주요 개념:
 * 1. RouterFunction: 라우팅 규칙 정의
 * 2. HandlerFunction: 요청을 처리하는 함수
 * 3. RequestPredicate: 요청 조건 (경로, HTTP 메서드, 헤더 등)
 * 4. route(): 라우트 생성
 * 5. nest(): 중첩 라우트
 *
 * 사용 예시:
 * GET    /functional/users        -> 모든 사용자 조회
 * GET    /functional/users/{id}   -> 특정 사용자 조회
 * POST   /functional/users        -> 사용자 생성
 * PUT    /functional/users/{id}   -> 사용자 수정
 * DELETE /functional/users/{id}   -> 사용자 삭제
 */
@Configuration
public class RouterConfig {

    /**
     * 기본 라우터 함수 - 단순한 방식
     */
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route()
            // GET /functional/users - 모든 사용자 조회
            .GET("/functional/users", accept(MediaType.APPLICATION_JSON), handler::getAllUsers)

            // GET /functional/users/count - 사용자 수 조회 (/{id}보다 먼저 정의)
            .GET("/functional/users/count", handler::getUserCount)

            // GET /functional/users/search - 검색 (/{id}보다 먼저 정의)
            .GET("/functional/users/search", handler::searchUsers)

            // GET /functional/users/stream - SSE 스트리밍 (/{id}보다 먼저 정의)
            .GET("/functional/users/stream", handler::streamUsers)

            // GET /functional/users/{id} - 특정 사용자 조회
            .GET("/functional/users/{id}", accept(MediaType.APPLICATION_JSON), handler::getUserById)

            // POST /functional/users - 사용자 생성
            .POST("/functional/users", accept(MediaType.APPLICATION_JSON), handler::createUser)

            // PUT /functional/users/{id} - 사용자 수정
            .PUT("/functional/users/{id}", accept(MediaType.APPLICATION_JSON), handler::updateUser)

            // DELETE /functional/users/{id} - 사용자 삭제
            .DELETE("/functional/users/{id}", handler::deleteUser)

            .build();
    }

    /**
     * 중첩 라우터 함수 - 경로별로 그룹화
     */
    @Bean
    public RouterFunction<ServerResponse> nestedUserRoutes(UserHandler handler) {
        return route()
            .path("/api/v2/users", builder -> builder
                .nest(accept(MediaType.APPLICATION_JSON), nestedBuilder -> nestedBuilder
                    .GET("", handler::getAllUsers)
                    .GET("/{id}", handler::getUserById)
                    .POST("", handler::createUser)
                    .PUT("/{id}", handler::updateUser)
                    .DELETE("/{id}", handler::deleteUser)
                )
                .GET("/count", handler::getUserCount)
                .GET("/search", handler::searchUsers)
                .GET("/stream", handler::streamUsers)
            )
            .build();
    }

    /**
     * 고급 라우터 함수 - 조건부 라우팅
     */
    @Bean
    public RouterFunction<ServerResponse> advancedRoutes(UserHandler handler) {
        return route()
            // 특정 헤더가 있는 경우에만 처리
            .GET("/api/v3/users",
                accept(MediaType.APPLICATION_JSON)
                    .and(headers(headers ->
                        headers.asHttpHeaders().getFirst("X-API-Version") != null
                    )),
                handler::getAllUsers
            )

            // 쿼리 파라미터 조건
            .GET("/api/v3/users/filtered",
                queryParam("active", "true"),
                handler::getAllUsers
            )

            .build();
    }

    /**
     * 필터 적용 예제
     */
    @Bean
    public RouterFunction<ServerResponse> routesWithFilter(UserHandler handler) {
        return route()
            .GET("/api/v4/users", handler::getAllUsers)
            .filter((request, next) -> {
                // 요청 전처리 (예: 로깅)
                System.out.println("요청: " + request.method() + " " + request.path());

                // 다음 핸들러 실행
                return next.handle(request)
                    .doOnNext(response -> {
                        // 응답 후처리 (예: 로깅)
                        System.out.println("응답 상태: " + response.statusCode());
                    });
            })
            .build();
    }
}

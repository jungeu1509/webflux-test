# Spring Boot WebFlux 예제 프로젝트

Spring WebFlux를 사용한 반응형(Reactive) 웹 애플리케이션 예제입니다.

## 기술 스택

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring WebFlux** - 반응형 웹 프레임워크
- **Project Reactor** - Mono, Flux를 활용한 반응형 프로그래밍
- **Gradle 9.0.0** - 빌드 도구

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── Main.java                          # 메인 애플리케이션
└── webflux/
    ├── controller/
    │   └── UserController.java        # REST API Controller
    ├── handler/
    │   └── UserHandler.java           # Functional Endpoints Handler
    ├── config/
    │   └── RouterConfig.java          # Router Functions 설정
    ├── service/
    │   └── UserService.java           # 비즈니스 로직
    ├── repository/
    │   ├── UserRepository.java        # Repository 인터페이스
    │   └── InMemoryUserRepository.java # In-Memory 구현체
    └── model/
        └── User.java                  # User 도메인 모델
```

## 빌드 및 실행

### 1. 프로젝트 빌드

```bash
./gradlew build
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IDE에서 `Main.java`를 직접 실행할 수 있습니다.

### 3. 테스트 실행

```bash
./gradlew test
```

## API 엔드포인트

애플리케이션이 시작되면 `http://localhost:8080`에서 접근 가능합니다.

### REST API (Controller 기반)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | 모든 사용자 조회 |
| GET | `/api/users/{id}` | 특정 사용자 조회 |
| POST | `/api/users` | 사용자 생성 |
| PUT | `/api/users/{id}` | 사용자 수정 |
| DELETE | `/api/users/{id}` | 사용자 삭제 |
| GET | `/api/users/stream` | SSE (Server-Sent Events) 스트리밍 |
| GET | `/api/users/count` | 사용자 수 조회 |

### Functional Endpoints (Router Functions)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/functional/users` | 모든 사용자 조회 |
| GET | `/functional/users/{id}` | 특정 사용자 조회 |
| POST | `/functional/users` | 사용자 생성 |
| PUT | `/functional/users/{id}` | 사용자 수정 |
| DELETE | `/functional/users/{id}` | 사용자 삭제 |

## API 사용 예제

### 1. 모든 사용자 조회
```bash
curl http://localhost:8080/api/users
```

### 2. 특정 사용자 조회
```bash
curl http://localhost:8080/api/users/1
```

### 3. 사용자 생성
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "홍길동",
    "email": "hong@example.com",
    "age": 25
  }'
```

### 4. 사용자 수정
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "김철수",
    "email": "kim@example.com",
    "age": 30
  }'
```

### 5. 사용자 삭제
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### 6. SSE 스트리밍 (실시간 데이터 수신)
```bash
curl -N http://localhost:8080/api/users/stream
```

## 주요 개념

### Reactive Programming
- **Mono**: 0~1개의 데이터를 비동기적으로 처리
- **Flux**: 0~N개의 데이터 스트림을 비동기적으로 처리
- **Non-blocking I/O**: 블로킹 없이 효율적인 리소스 사용

### Controller vs Router Functions

#### Controller 방식 (어노테이션 기반)
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public Flux<User> getUsers() { ... }
}
```

#### Router Functions 방식 (함수형)
```java
@Bean
public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
    return route()
        .GET("/functional/users", handler::getUsers)
        .build();
}
```

## 테스트

프로젝트에는 Reactive 스트림 테스트를 위한 예제가 포함되어 있습니다:

- **ReactiveTestExample.java**: StepVerifier를 사용한 20가지 테스트 예제
  - Mono/Flux 기본 테스트
  - 에러 처리 테스트
  - 연산자 테스트 (map, filter, flatMap 등)
  - 비즈니스 로직 테스트

```bash
./gradlew test
```

## 학습 포인트

1. **Spring WebFlux** - 반응형 웹 프레임워크 기본
2. **Project Reactor** - Mono, Flux 활용법
3. **Non-blocking I/O** - 비동기 처리 패턴
4. **StepVerifier** - 반응형 스트림 테스트 방법
5. **Controller vs Router Functions** - 두 가지 라우팅 방식 비교

## 참고 자료

- [Spring WebFlux 공식 문서](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor 공식 문서](https://projectreactor.io/docs)
- [Reactive Streams 스펙](https://www.reactive-streams.org/)

package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
public class Main {


    /**
     * Spring Boot WebFlux 애플리케이션
     *
     * 실행 방법:
     * 1. Maven: mvn spring-boot:run
     * 2. Gradle: ./gradlew bootRun
     * 3. IDE에서 직접 실행
     *
     * 애플리케이션이 시작되면:
     * - http://localhost:8080/api/users (REST API - Controller)
     * - http://localhost:8080/functional/users (Functional Endpoints)
     *
     * @SpringBootApplication 어노테이션은 다음을 포함합니다:
     * - @Configuration: 설정 클래스
     * - @EnableAutoConfiguration: 자동 설정
     * - @ComponentScan: 컴포넌트 스캔
     */

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Spring Boot WebFlux 애플리케이션이 시작되었습니다!");
        System.out.println("=".repeat(60));
        System.out.println("\n📌 REST API 엔드포인트 (Controller 기반):");
        System.out.println("  - GET    http://localhost:8080/api/users");
        System.out.println("  - GET    http://localhost:8080/api/users/{id}");
        System.out.println("  - POST   http://localhost:8080/api/users");
        System.out.println("  - PUT    http://localhost:8080/api/users/{id}");
        System.out.println("  - DELETE http://localhost:8080/api/users/{id}");
        System.out.println("  - GET    http://localhost:8080/api/users/stream (SSE)");
        System.out.println("  - GET    http://localhost:8080/api/users/count");

        System.out.println("\n📌 Functional 엔드포인트 (Router Functions):");
        System.out.println("  - GET    http://localhost:8080/functional/users");
        System.out.println("  - GET    http://localhost:8080/functional/users/{id}");
        System.out.println("  - POST   http://localhost:8080/functional/users");
        System.out.println("  - PUT    http://localhost:8080/functional/users/{id}");
        System.out.println("  - DELETE http://localhost:8080/functional/users/{id}");

        System.out.println("\n💡 테스트 방법:");
        System.out.println("  curl http://localhost:8080/api/users");
        System.out.println("  curl http://localhost:8080/api/users/1");
        System.out.println("  curl -X POST http://localhost:8080/api/users \\");
        System.out.println("       -H \"Content-Type: application/json\" \\");
        System.out.println("       -d '{\"name\":\"테스트\",\"email\":\"test@example.com\",\"age\":25}'");

        System.out.println("\n" + "=".repeat(60) + "\n");
    }
}

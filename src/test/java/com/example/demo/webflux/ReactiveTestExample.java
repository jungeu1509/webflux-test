package com.example.demo.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * Reactive 테스트 예제
 *
 * StepVerifier란?
 * - Reactor 테스트 라이브러리에서 제공하는 테스트 도구
 * - Mono/Flux의 동작을 단계별로 검증
 * - 비동기 스트림의 완료, 에러, 발행된 값을 테스트
 *
 * 주요 메서드:
 * - expectNext(): 다음 값 검증
 * - expectNextCount(): 특정 개수만큼 값이 발행되는지 검증
 * - expectComplete(): 정상 완료 검증
 * - expectError(): 에러 발생 검증
 * - verifyComplete(): 완료 확인 및 검증 종료
 */
public class ReactiveTestExample {

    /**
     * 1. Mono 기본 테스트
     */
    @Test
    public void testMono() {
        Mono<String> mono = Mono.just("Hello WebFlux");

        StepVerifier.create(mono)
            .expectNext("Hello WebFlux")  // 다음 값 검증
            .verifyComplete();             // 완료 확인
    }

    /**
     * 2. Flux 기본 테스트
     */
    @Test
    public void testFlux() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);

        StepVerifier.create(flux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .expectNext(5)
            .verifyComplete();
    }

    /**
     * 3. Flux 개수 검증
     */
    @Test
    public void testFluxCount() {
        Flux<Integer> flux = Flux.range(1, 100);

        StepVerifier.create(flux)
            .expectNextCount(100)  // 100개의 값이 발행되는지 검증
            .verifyComplete();
    }

    /**
     * 4. 빈 Mono 테스트
     */
    @Test
    public void testEmptyMono() {
        Mono<String> emptyMono = Mono.empty();

        StepVerifier.create(emptyMono)
            .expectNextCount(0)  // 값이 없음
            .verifyComplete();   // 하지만 정상 완료
    }

    /**
     * 5. 에러 처리 테스트
     */
    @Test
    public void testMonoError() {
        Mono<String> errorMono = Mono.error(new RuntimeException("에러 발생!"));

        StepVerifier.create(errorMono)
            .expectError(RuntimeException.class)  // RuntimeException 발생 검증
            .verify();
    }

    /**
     * 6. 특정 에러 메시지 검증
     */
    @Test
    public void testErrorMessage() {
        Mono<String> errorMono = Mono.error(new IllegalArgumentException("잘못된 인자"));

        StepVerifier.create(errorMono)
            .expectErrorMatches(throwable ->
                throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals("잘못된 인자")
            )
            .verify();
    }

    /**
     * 7. map 연산 테스트
     */
    @Test
    public void testMap() {
        Flux<Integer> flux = Flux.just(1, 2, 3)
            .map(num -> num * 2);

        StepVerifier.create(flux)
            .expectNext(2, 4, 6)  // 여러 값을 한번에 검증
            .verifyComplete();
    }

    /**
     * 8. filter 연산 테스트
     */
    @Test
    public void testFilter() {
        Flux<Integer> flux = Flux.range(1, 10)
            .filter(num -> num % 2 == 0);  // 짝수만

        StepVerifier.create(flux)
            .expectNext(2, 4, 6, 8, 10)
            .verifyComplete();
    }

    /**
     * 9. flatMap 테스트
     */
    @Test
    public void testFlatMap() {
        Flux<String> flux = Flux.just("A", "B")
            .flatMap(letter -> Flux.just(letter + "1", letter + "2"));

        StepVerifier.create(flux)
            .expectNextCount(4)  // A1, A2, B1, B2
            .verifyComplete();
    }

    /**
     * 10. onErrorReturn 테스트
     */
    @Test
    public void testOnErrorReturn() {
        Flux<Integer> flux = Flux.just(1, 2, 0, 4)
            .map(num -> 10 / num)  // 0으로 나누면 에러
            .onErrorReturn(-1);     // 에러 시 -1 반환

        StepVerifier.create(flux)
            .expectNext(10)    // 10 / 1
            .expectNext(5)     // 10 / 2
            .expectNext(-1)    // 에러 -> -1
            .verifyComplete();
    }

    /**
     * 11. onErrorResume 테스트
     */
    @Test
    public void testOnErrorResume() {
        Mono<String> mono = Mono.<String>error(new RuntimeException("에러"))
            .onErrorResume(error -> Mono.just("대체 값"));

        StepVerifier.create(mono)
            .expectNext("대체 값")
            .verifyComplete();
    }

    /**
     * 12. timeout 테스트
     */
    @Test
    public void testTimeout() {
        Mono<String> mono = Mono.delay(Duration.ofSeconds(5))
            .map(tick -> "완료")
            .timeout(Duration.ofSeconds(1));  // 1초 타임아웃

        StepVerifier.create(mono)
            .expectError()  // 타임아웃 에러 발생
            .verify();
    }

    /**
     * 13. zip 연산 테스트
     */
    @Test
    public void testZip() {
        Mono<String> mono1 = Mono.just("Hello");
        Mono<String> mono2 = Mono.just("WebFlux");

        Mono<String> result = Mono.zip(mono1, mono2)
            .map(tuple -> tuple.getT1() + " " + tuple.getT2());

        StepVerifier.create(result)
            .expectNext("Hello WebFlux")
            .verifyComplete();
    }

    /**
     * 14. take 연산 테스트
     */
    @Test
    public void testTake() {
        Flux<Integer> flux = Flux.range(1, 100)
            .take(5);  // 처음 5개만

        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }

    /**
     * 15. distinctUntilChanged 테스트
     */
    @Test
    public void testDistinctUntilChanged() {
        Flux<Integer> flux = Flux.just(1, 1, 2, 2, 1, 3, 3, 2)
            .distinctUntilChanged();  // 연속 중복 제거

        StepVerifier.create(flux)
            .expectNext(1, 2, 1, 3, 2)
            .verifyComplete();
    }

    /**
     * 16. collectList 테스트
     */
    @Test
    public void testCollectList() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);
        Mono<java.util.List<Integer>> listMono = flux.collectList();

        StepVerifier.create(listMono)
            .expectNextMatches(list ->
                list.size() == 5 &&
                    list.get(0) == 1 &&
                    list.get(4) == 5
            )
            .verifyComplete();
    }

    /**
     * 17. reduce 테스트
     */
    @Test
    public void testReduce() {
        Mono<Integer> sum = Flux.range(1, 10)
            .reduce((acc, value) -> acc + value);  // 합계

        StepVerifier.create(sum)
            .expectNext(55)  // 1+2+3+...+10 = 55
            .verifyComplete();
    }

    /**
     * 18. concat 테스트
     */
    @Test
    public void testConcat() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<String> flux2 = Flux.just("C", "D");

        Flux<String> concatenated = Flux.concat(flux1, flux2);

        StepVerifier.create(concatenated)
            .expectNext("A", "B", "C", "D")
            .verifyComplete();
    }

    /**
     * 19. merge 테스트
     */
    @Test
    public void testMerge() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<String> flux2 = Flux.just("C", "D");

        Flux<String> merged = Flux.merge(flux1, flux2);

        StepVerifier.create(merged)
            .expectNextCount(4)  // 순서는 보장되지 않으므로 개수만 검증
            .verifyComplete();
    }

    /**
     * 20. 실제 비즈니스 로직 테스트 예제
     */
    @Test
    public void testBusinessLogic() {
        // 예: 성인 사용자만 필터링하는 로직
        Flux<Integer> ages = Flux.just(15, 20, 17, 25, 30, 16);
        Flux<Integer> adults = ages.filter(age -> age >= 18);

        StepVerifier.create(adults)
            .expectNext(20, 25, 30)
            .verifyComplete();
    }
}

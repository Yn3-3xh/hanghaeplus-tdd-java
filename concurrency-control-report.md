# 동시성 제어 방식
동시성 제어는 여러 프로세스나 스레드가 공유 자원에 접근할 때의 일관성과 무결성을 보장하는 다양한 전략입니다.   
이 동시성 제어 방식은 크게 두 가지 카테고리로 나뉘게 됩니다.

## 1. 비관적(Pessimistic) 동시성 제어
충돌이 많이 발생할 것이라고 가정하고 동시성을 제어하는 방식으로,   
데이터에 접근하기 전에 미리 Lock을 걸고, 다른 트랜잭션이 접근할 수 없도록 합니다.   
(이 방식은 주로 쓰기 작업이 많은 환경에서 효과적입니다.)

**동작 방식**
1. Lock 획득
   
    트랜잭션이 특정 데이터에 접근하고자 하면 먼저 해당 데이터에 대한 Lock을 획득해야 합니다.
2. 데이터 접근

    Lock을 획득하면 데이터에 접근하여 작업을 수행합니다.   
    Lock이 유지되는 동안에는 다른 트랜잭션은 해당 데이터에 접근할 수 없습니다.
3. Lock 해제

    트랜잭션이 완료되면 Lock을 해제합니다.   
    Lock 해제는 트랜잭션이 커밋/롤백 되었을 때 이뤄집니다.
4. 교착 상태 처리

    비관적 동시성 제어는 서로의 Lock이 풀리기를 기다리는 교착 상태를 처리해야 합니다.
    > **교착 상태 처리 방법**   
      타임아웃: 일정시간 동안 Lock을 획득하지 못하면 트랜잭션을 롤백   
      교착 상태 감지: 주기적으로 교착 상태를 감지하고, 교차 상태에 있는 트랜잭션 중 하나를 롤백   
      교착 상태 예방: 트랜잭션이 Lock을 획득하는 순서를 정하여 교착 상태가 발생하지 않도록 예방

**장점**   
* 데이터 무결성 보장
* 동시에 데이터가 변경되는 것을 방지하여 일관성 유지

**단점**   
* 성능 저하
  * Lock을 사용하므로, 스레드 간의 대기 발생 가능
* 교착 상태 발생
  * 두 개 이상의 프로세스 서로의 Lock을 기다리는 상황에서 시스템 다운 가능

## 2. 낙관적(Optimistic) 동시성 제어
충돌이 많이 발생하지 않을 것이라고 가정하고 동시성을 제어하는 방식으로,   
주로 읽기 작업이 많고, 쓰기 작업이 적은 환경에서 효과적입니다.

**동작 방식**
1. 트랜잭션 시작 및 작업 수행
    
    트랜잭션은 필요한 읽기 및 계산 작업을 수행합니다.   
    이 단계에서 실제로 DB의 데이터는 수정되지 않고, 변경될 데이터를 로컬 변수 혹은 임시 데이터 구조에 저장합니다.
2. 결과 검증
   
    트랜잭션이 종료되면, DB는 해당 트랜잭션이 실행되는 동안 다른 트랜잭션에 의해 DB의 데이터가 변경되었는지를 검증합니다.   
    
    > **검증 방식**   
      타임스탬프 비교: 각 데이터에 타임스탬프를 부여하여 트랜잭션이 시작도니 이후에 변경된 데이터가 있는지 확인   
      버전 관리: 각 데이터에 버전을 부여하여 트랜잭션이 시작된 이후에 버전이 변경되었는지 확인
3. 커밋/롤백
   검증 단계에서 충돌이 발생하지 않으면 DB에 변경 사항을 커밋합니다.   
   만약 충돌이 발생했다면 트랜잭션은 롤백되고 재시도 됩니다.

**장점**
* 성능 향상
  * 많은 경우에 Lock이 필요하지 않으므로 병렬성 증가
* 교착 상태 방지
  * 각 트랜잭션이 독립적으로 작업하므로 교착 상태의 위험 감소

**단점**
* 충돌 처리
  * 충돌 발생 시 롤백에 의한 비용 증가
* 높은 충동률에서의 성능 저하 가능

---

# 동시성 제어 키워드

## 1. synchronized
공유 자원이 사용되어 동기화가 필요한 부분을 임계영역이라 부르며, 이 영역에서 여러 스레드의 동시 접근을 방지하기 위해 `synchronized` 키워드를 사용합니다.
이는 특정 메서드나 코드 블럭에 적용되어, 한 번에 하나의 스레드만 이 영역에 진입할 수 있도록 Lock을 걸어줍니다.

**동작 방식**   
`synchronized` 키워드를 통해 지정된 임계영역에 한 스레드가 접근하면, 다른 스레드는 대기하게 됩니다.
해당 스레드가 임계영역의 작업을 마친 후 unlock 상태가 되면, 대기 중이던 다른 스레드가 차례대로 이 영역에 접근합니다.

**장점**   
* 데이터 일관성 보장
  * 여러 스레드가 하나의 자원에 동시 접근하는 상황에서 데이터 무결성을 유지
* 간단하고 직관적
  * 사용법이 매우 간단
  * 자바 언어에서 기본 제공되는 메커니즘이므로 추가적인 라이브러리 없이 쉽게 적용 가능

**단점**
* 성능 저하
  * 여러 스레드가 대기하게 되어 병목 현상이 발생 가능
* 교착 상태
  * 잘못된 사용으로 두 스레드가 서로의 잠금을 기다리며 교착 상태 발생 가능

## 2. ReentrantLock
자바에서 제공하는 동시성 제어 기법으로, `synchronized`와 유사하지만 더 세밀한 제어를 제공합니다.
이름 그대로 한 스레드가 동일한 락을 여러 번 획득할 수 있는 재진입 가능(reentrant) 특성을 가지고 있습니다.

**동작 방식**   
명시적으로 Lock을 획득/해제하는 메서디인 `lock()`, `unlock()`을 사용하여 제어합니다.   
tryLock() 등의 비차단적 Lock 획득 방법도 제공하여, 스레드가 계속해서 대기하지 않고 일정 시간 동안만 Lock을 기다리도록 설정할 수도 있습니다.

**장점**
* 세밀한 제어
  * Lock을 명시적으로 관리할 수 있어서 더 복잡한 동시성 상황에서 유연하게 사용 가능
* 타임아웃 및 조건 변수
  * 타임아웃 설정과 조건 변수를 통해 더욱 정교한 동기화 작업 가능

**단점**
* 복잡성 증가
  * 직접 Lock을 획득하고 해제해야 하므로 잘못된 사용으로 인해 교착 상태나 버그 발생 가능
* 오버헤드
  * `synchronized`보다 조금 더 복잡한 구조로 인해 성능 오버헤드 발생 가능

## 3. Atomic Variables
Atomic 변수는 멀티스레드 환경에서 원자적(atomic)으로 동작하는 변수로, 자바의 java.util.concurrent.atomic 패키지에서 제공됩니다.   
이는 일반 변수와 달리 동시에 여러 스레드가 값을 읽거나 수정할 때 충돌 없이 안전하게 동작합니다.

**동작 방식**   
내부적으로 CAS(Compare-And-Swap) 기법을 사용하여 값이 변경되는 동안 다른 스레드의 접근을 방지합니다.   

**장점**
* 간편한 동기화
  * 복잡한 Lock을 사용하지 않고도 원자적인 연산을 보장
* 빠른 성능
  * CAS 연산은 Lock을 사용하지 않기 때문에 상대적으로 더 빠른 성능을 제공

**단점**
* 제한된 연산
  * 기본적인 숫자 연산과 비교 정도만 원자적으로 처리할 수 있으며, 복잡한 연산에는 부적합
* 충돌 가능성
  * 매우 빈번한 충돌 상황에서는 성능 저하 가능

## 4. ConcurrentHashMap
자바의 동시성 컬렉션 중 하나로, 여러 스레드가 동시에 안전하게 값을 읽고 쓸 수 있는 HashMap입니다.
일반적인 HashMap은 thread-safe하지 않기 때문에, 여러 스레드가 동시에 값을 삽입하거나 삭제할 경우 문제가 발생할 수 있습니다.   
하지만 ConcurrentHashMap은 내부적으로 Lock을 적절히 나누어 lock contention을 최소화합니다.

**동작 방식**   
내부적으로 여러 bucket에 나누어 Lock을 걸어, 한 번에 여러 스레드가 각기 다른 bucket에 접근할 수 있도록 합니다.   
전체 Map에 Lock을 거는 것이 아니라 특정 bucket에만 Lock을 거므로 성능이 높습니다.

**장점**
* 높은 병렬성
  * 다수의 스레드가 동시에 데이터를 읽고 쓸 수 있어 성능이 우수
* 스레드 안전성
  * 멀티스레드 환경에서도 안전하게 데이터 처리 가능

**단점**
* 부분 Lock
  * 특정 상황에서는 bucket Lock이 걸리기 때문에 여전히 성능 저하 발생 가능
* 복잡성
  * 단순한 동시성 처리보다는 다소 복잡한 구조

## 5. CompletableFuture
비동기 작업을 관리하는 자바의 동시성 제어 도구로, 콜백 방식으로 비동기 연산의 결과를 처리할 수 있게 해줍니다.   
비동기적으로 작업을 실행하면서도, 그 결과를 나중에 받아서 처리할 수 있습니다.

**동작 방식**   
`supplyAsync()`, `thenApply()` 등의 메서드를 통해 비동기 작업을 정의하고, 완료된 후에 실행할 후속 작업을 설정할 수 있습니다.   
작업이 완료되면 자동으로 결과가 전달되며, 완료 여부에 상관없이 특정 작업을 수행할 수 있는 메서드도 제공합니다.

**장점**
* 비동기 처리
  * 작업을 비동기적으로 처리하여 I/O 대기 등으로 인해 발생하는 성능 저하 감소
* 유연한 체이닝
  * 여러 작업을 연속으로 처리할 때 각 작업을 체이닝하여 작성할 수 있어서 가독성과 유지보수성이 높음

**단점**
* 디버깅 어려움
  * 비동기적으로 작업이 진행되기 때문에, 디버깅이 다소 복잡
* 복잡성
  * 복잡한 비동기 흐름을 처리할 때 코드의 복잡성이 증가

# 나의 선택
1. `syncronized`처럼 암묵적이기 보다는 명시적인 ReentrantLock를 사용하자.
2. return 값을 반환 받을 수 있도록 @Async 대신 CompletableFuture를 적용하자.

> * AtomicLong을 사용하려면 UserPoint의 point 필드 타입을 변경해야 하는데, 그러면 사용했던 모든 테스트를 변경해야 하는데 이게 맞을지? 생각이 들었습니다.
> * ConcurrentHashMap를 사용하면 좋을 거 같은데 러닝커브가 클 것 같았습니다.    
> * 큐로 구현을 하려고 생각했는데 위와 같은 러닝커브와 복잡해지는 것 같았습니다.   
> 
> 따라서, 잘 모르는 상태에서 간단하게 적용할 수 있는 Lock을 동시성 제어에 대해 익혀보고자 사용했습니다. 

---

# ReentrantLock + CompletableFuture
처음에는 동시성을 제어하기 위해 CompletableFuture를 선택하면 될 것이라고 생각하고 개발했지만,   
테스트를 하면서 CompletableFuture는 동시성을 제어하기 위한 것이 아닌, 비동기를 위한 것이라는 것을 알았고,   
이후에 ReentrantLock을 걸어서 해결했습니다.

* 적용 전
    ```java
    @Override
    public UserPoint charge(Long id, Long amount) {
        pointValidator.validateChargeAble(id, amount);
    
        UserPoint userPoint = userPointTable.selectById(id);
        long pointSum = userPoint.add(amount);
    
        UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, pointSum);
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, savedUserPoint.updateMillis());
        return savedUserPoint;
    }
    ```

* 적용 후
    ```java
    @Override
    public CompletableFuture<UserPoint> charge(Long id, Long amount) {
        return CompletableFuture.supplyAsync(() -> {
            lock.lock();
            try {
                pointValidator.validateChargeAble(id, amount);

                UserPoint userPoint = userPointTable.selectById(id);
                long pointSum = userPoint.add(amount);

                UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, pointSum);
                pointHistoryTable.insert(id, amount, TransactionType.CHARGE, savedUserPoint.updateMillis());
                return savedUserPoint;
            } finally {
                lock.unlock();
            }
        });
    }
    ```
  * ReentrantLock 의존성
  
    PointService에서 `private final ReentrantLock lock = new ReentrantLock();`이 아닌 OCP와 DIP를 지켜`private final ReentrantLock lock;`로 작성하고자,   
    아래 코드와 같이 빈을 설정했습니다.
    ```java
    @Configuration
    public class PointConfig {
    
        @Bean
        public ReentrantLock reentrantLock() {
            return new ReentrantLock();
        }
    }
    ```

## 테스트
* 포인트 충전 동시성 테스트
    ```java
    @Test
    @DisplayName("포인트 충전 동시성 - 통과")
    void pass_chargeConcurrentTest() {
        // given
        long id = 1L;
        long addAmount = 100L;
        int range = 10;
    
        // when
        List<CompletableFuture<UserPoint>> futures = IntStream.range(0, range)
                .mapToObj(i -> sut.charge(id, addAmount)).toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
    
        // then
        UserPoint savedUserPoint = userPointTable.selectById(id);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
    
        assertThat(pointHistories.size()).isEqualTo(range);
        assertThat(savedUserPoint.point()).isEqualTo(addAmount * range);
    }
    
    @Test
    @DisplayName("포인트 충전 동시성 - 초과 실패")
    void fail_chargeConcurrentTest_maxPoint() {
        // given
        long id = 1L;
        long addAmount = 1000L;
        int range = 11;
        int passRange = 10;
    
        // when
        List<CompletableFuture<UserPoint>> futures = IntStream.range(0, range)
                .mapToObj(i -> sut.charge(id, addAmount)).toList();
        CompletionException result = assertThrows(CompletionException.class, () -> {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();
        });
    
        // then
        Throwable cause = result.getCause();
        assertThat(cause).isInstanceOf(IllegalArgumentException.class);
        assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.EXCEED_MAX_POINT.getMessage());
    
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories.size()).isEqualTo(passRange);
    }
    ```

* 포인트 사용 동시성 테스트
    ```
    @Test
    @DisplayName("포인트 사용 동시성 - 통과")
    void pass_useConcurrentTest() {
        // given
        long id = 1L;
        long amount = 1000L;
        long subAmount = 100L;
        int range = 10;
        userPointTable.insertOrUpdate(id, amount);

        // when
        List<CompletableFuture<UserPoint>> futures = IntStream.range(0, range)
                .mapToObj(i -> sut.use(id, subAmount)).toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();

        // then
        UserPoint savedUserPoint = userPointTable.selectById(id);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

        assertThat(pointHistories.size()).isEqualTo(range);
        assertThat(savedUserPoint.point()).isZero();
    }

    @Test
    @DisplayName("포인트 사용 동시성 - 실패")
    void fail_useConcurrentTest() {
        // given
        long id = 1L;
        long amount = 1000L;
        long subAmount = 100L;
        int range = 11;
        int passRange = 10;
        userPointTable.insertOrUpdate(id, amount);

        // when
        List<CompletableFuture<UserPoint>> futures = IntStream.range(0, range)
                .mapToObj(i -> sut.use(id, subAmount)).toList();
        CompletionException result = assertThrows(CompletionException.class, () -> {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();
        });

        // then
        Throwable cause = result.getCause();
        assertThat(cause).isInstanceOf(IllegalArgumentException.class);
        assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_POINT.getMessage());

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories.size()).isEqualTo(passRange);
    }
    ```

* 포인트 충전/사용 융합 테스트
    ```
    @Nested
    @DisplayName("포인트 충전/사용 혼합 동시성 테스트")
    class chargeUseConcurrentTest {
        @Test
        @DisplayName("포인트 충전/사용 혼합 동시성 테스트 - 통과")
        void pass_chargeUseConcurrentTest() {
            // given
            long id = 1L;

            // when
            List<CompletableFuture<UserPoint>> futures = Arrays.asList(
                    sut.charge(id, 1500L),
                    sut.charge(id, 2000L),
                    sut.use(id, 1500L)
            );
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();

            // then
            UserPoint result = userPointTable.selectById(id);
            assertThat(result.point()).isEqualTo(2000L);

            List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
            assertThat(pointHistories.get(0).amount()).isEqualTo(1500L);
            assertThat(pointHistories.get(0).type()).isEqualTo(TransactionType.CHARGE);

            assertThat(pointHistories.get(1).amount()).isEqualTo(2000L);
            assertThat(pointHistories.get(1).type()).isEqualTo(TransactionType.CHARGE);

            assertThat(pointHistories.get(2).amount()).isEqualTo(1500L);
            assertThat(pointHistories.get(2).type()).isEqualTo(TransactionType.USE);
        }

        @Test
        @DisplayName("포인트 충전/사용 혼합 동시성 테스트 - 초과 실패")
        void fail_chargeUseConcurrentTest_maxPoint() {
            // given
            long id = 1L;

            // when
            CompletableFuture<UserPoint> future1 = sut.charge(id, 5000L);
            CompletableFuture<UserPoint> future2 = future1.thenCompose(u -> sut.charge(id, 4000L));
            CompletableFuture<UserPoint> future3 = future2.thenCompose(u -> sut.charge(id, 2000L));
            CompletableFuture<UserPoint> future4 = future3.thenCompose(u -> sut.charge(id, 3000L));

            CompletionException result = assertThrows(CompletionException.class, () -> {
                future4.join();
            });

            // then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.EXCEED_MAX_POINT.getMessage());

            UserPoint savedUserPoint = userPointTable.selectById(id);
            assertThat(savedUserPoint.point()).isEqualTo(9000L);

            List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
            assertThat(pointHistories.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("포인트 충전/사용 혼합 동시성 테스트 - 잔고 부족 실패")
        void fail_chargeUseConcurrentTest_notUsedPoint() {
            // given
            long id = 1L;

            // when
            CompletableFuture<UserPoint> future1 = sut.charge(id, 1500L);
            CompletableFuture<UserPoint> future2 = future1.thenCompose(u -> sut.charge(id, 500L));
            CompletableFuture<UserPoint> future3 = future2.thenCompose(u -> sut.use(id, 2500L));
            CompletableFuture<UserPoint> future4 = future3.thenCompose(u -> sut.charge(id, 3000L));

            CompletionException result = assertThrows(CompletionException.class, () -> {
                future4.join();
            });

            // then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_POINT.getMessage());

            UserPoint savedUserPoint = userPointTable.selectById(id);
            assertThat(savedUserPoint.point()).isEqualTo(2000L);

            List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
            assertThat(pointHistories.size()).isEqualTo(2);
        }
    }
    ```

---

# 나의 생각
TDD를 적용하는 flow와 불필요한 단위/통합 테스트가 무엇인지, 동시성 제어는 어떻게 해야 하는지에 대해 조금 더 알게 되었습니다.   
이후에는 ReentrantLock 대신 ConcurrentHashMap을 사용을 하고,   
사용한 후에는 비즈니스 로직에서 동시성 관련 책임은 AOP로 분리해서 동시성 제어를 하는 애노테이션을 거는 방식으로 하면 좋겠다고 생각이 들었습니다. 
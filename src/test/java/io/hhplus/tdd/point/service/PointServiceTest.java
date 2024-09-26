package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.error.PointErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("PointService 통합테스트")
class PointServiceTest {

    @Autowired
    private PointService sut;

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Nested
    @DisplayName("포인트 조회 테스트")
    class selectTest {
        @Test
        @DisplayName("포인트 조회 - id 검증 실패")
        void fail_selectTest_nullId() {
            // given
            Long id = null;

            // when
            IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> {
                sut.select(id);
            });

            // then
            assertThat(result.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_ID.getMessage());
        }

        @Test
        @DisplayName("포인트 조회 - 통과")
        void pass_selectTest() {
            // given
            long id = 1L;
            long amount = 1000L;
            userPointTable.insertOrUpdate(id, amount);

            // when
            UserPoint result = sut.select(id);

            // then
            assertThat(result.point()).isEqualTo(amount);
        }
    }

    @Nested
    @DisplayName("포인트 충전/사용 내역 조회 테스트")
    class historyTest {
        @Test
        @DisplayName("포인트 충전/사용 내역 조회 - id 검증 실패")
        void fail_historyTest_nullId() {
            // given
            Long id = null;

            // when
            IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> {
                sut.history(id);
            });

            // then
            assertThat(result.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_ID.getMessage());
        }

        @Test
        @DisplayName("포인트 충전/사용 내역 조회 - 통과")
        void pass_historyTest() {
            // given
            long id = 1L;
            long chargeAmount = 1000L;
            long useAmount = 500L;
            long chargeUpdateMillis = 1L;
            long useUpdateMillis = 2L;

            pointHistoryTable.insert(id, chargeAmount, TransactionType.CHARGE, chargeUpdateMillis);
            pointHistoryTable.insert(id, useAmount, TransactionType.USE, useUpdateMillis);

            // when
            List<PointHistory> pointHistories = sut.history(id);

            // then
            assertThat(pointHistories.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("포인트 충전 테스트")
    class chargeTest {
        @Test
        @DisplayName("포인트 충전 - id 검증 실패")
        void fail_chargeTest_nullId() {
            // given
            Long id = null;
            Long amount = 1L;

            // when
            CompletionException result = assertThrows(CompletionException.class, () -> {
                CompletableFuture<UserPoint> future = sut.charge(id, amount);
                future.join();
            });

            // then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_ID.getMessage());
        }

        @Test
        @DisplayName("포인트 충전 - amount 검증 실패")
        void fail_chargeTest_amount() {
            // given
            Long id = 1L;
            Long amount = 0L;

            // when
            CompletionException result = assertThrows(CompletionException.class, () -> {
                CompletableFuture<UserPoint> future = sut.charge(id, amount);
                future.join();
            });

            // then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getMessage());
        }

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
    }

    @Nested
    @DisplayName("포인트 사용 테스트")
    class useTest {
        @Test
        @DisplayName("포인트 사용 - id 검증 실패")
        void fail_useTest_nullId() {
            // given
            Long id = null;
            Long amount = 1L;

            // when
            CompletionException result = assertThrows(CompletionException.class, () -> {
                CompletableFuture<UserPoint> future = sut.use(id, amount);
                future.join();
            });

            // then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_ID.getMessage());
        }

        @Test
        @DisplayName("포인트 사용 - amount 검증 실패")
        void fail_useTest_amount() {
            // given
            Long id = 1L;
            Long amount = 0L;

            // when
            CompletionException result = assertThrows(CompletionException.class, () -> {
                CompletableFuture<UserPoint> future = sut.use(id, amount);
                future.join();
            });

            // then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getMessage());
        }

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
    }

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
}
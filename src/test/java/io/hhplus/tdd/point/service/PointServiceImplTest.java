package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.error.PointErrorMessage;
import io.hhplus.tdd.point.validator.PointValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletionException;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointService 단위테스트")
class PointServiceImplTest {

    @InjectMocks
    private PointServiceImpl sut;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private PointValidator pointValidator;

    @Mock
    private ReentrantLock lock;

    @Nested
    @DisplayName("포인트 충전")
    class chargeTest {
        @Test
        @DisplayName("포인트 충전 - 유효")
        void chargeTest_valid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long addAmount = 4000L;
            long totalAmount = baseAmount + addAmount;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);
            UserPoint savedUserPoint = new UserPoint(id, totalAmount, updateMillis);

            when(userPointTable.selectById(id)).thenReturn(userPoint);
            when(userPointTable.insertOrUpdate(id, totalAmount)).thenReturn(savedUserPoint);

            // When
            UserPoint result = sut.charge(id, addAmount).join();

            // Then
            assertThat(result.point()).isEqualTo(totalAmount);
        }

        @Test
        @DisplayName("포인트 충전 - 유효하지 않음")
        void chargeTest_invalid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long addAmount = 10000L;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);

            when(userPointTable.selectById(id)).thenReturn(userPoint);

            // When
            CompletionException result = assertThrows(CompletionException.class, () -> {
                sut.charge(id, addAmount).join();
            });

            // Then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.EXCEED_MAX_POINT.getMessage());
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    class useTest {
        @Test
        @DisplayName("포인트 사용 - 유효")
        void chargeTest_valid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long subAmount = 500L;
            long totalAmount = baseAmount - subAmount;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);
            UserPoint savedUserPoint = new UserPoint(id, totalAmount, updateMillis);

            when(userPointTable.selectById(id)).thenReturn(userPoint);
            when(userPointTable.insertOrUpdate(id, totalAmount)).thenReturn(savedUserPoint);

            // When
            UserPoint result = sut.use(id, subAmount).join();

            // Then
            assertThat(result.point()).isEqualTo(totalAmount);
        }

        @Test
        @DisplayName("포인트 사용 - 유효하지 않음")
        void chargeTest_invalid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long subAmount = 1500L;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);

            when(userPointTable.selectById(id)).thenReturn(userPoint);

            // When
            CompletionException result = assertThrows(CompletionException.class, () -> {
                sut.use(id, subAmount).join();
            });

            // Then
            Throwable cause = result.getCause();
            assertThat(cause).isInstanceOf(IllegalArgumentException.class);
            assertThat(cause.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_POINT.getMessage());
        }
    }
}
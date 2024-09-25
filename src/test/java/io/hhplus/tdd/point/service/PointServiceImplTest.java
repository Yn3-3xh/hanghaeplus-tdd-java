package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.validator.PointValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    @InjectMocks
    private PointServiceImpl sut;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private PointValidator pointValidator;

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
            UserPoint result = sut.charge(id, addAmount);

            // Then
            assertThat(result.point()).isEqualTo(totalAmount);
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
            UserPoint result = sut.use(id, subAmount);

            // Then
            assertThat(result.point()).isEqualTo(totalAmount);
        }
    }
}
package io.hhplus.tdd.point.entity;

import io.hhplus.tdd.point.error.PointErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UserPoint 단위테스트")
public class UserPointTest {

    @Nested
    @DisplayName("포인트 add")
    class addTest {
        @Test
        @DisplayName("포인트 add - 유효")
        void addTest_valid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long addAmount = 4000L;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);

            // When
            long result = userPoint.add(addAmount);

            // Then
            assertThat(result).isEqualTo(baseAmount + addAmount);
        }

        @Test
        @DisplayName("포인트 add - 유효하지 않음")
        void addTest_invalid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long addAmount = 10000L;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);

            // When
            IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> {
                userPoint.add(addAmount);
            });

            // Then
            assertThat(result.getMessage()).isEqualTo(PointErrorMessage.EXCEED_MAX_POINT.getMessage());
        }
    }

    @Nested
    @DisplayName("포인트 sub")
    class subTest {
        @Test
        @DisplayName("포인트 sub - 유효")
        void subTest_valid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long subAmount = 500L;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);

            // When
            long result = userPoint.sub(subAmount);

            // Then
            assertThat(result).isEqualTo(baseAmount - subAmount);
        }

        @Test
        @DisplayName("포인트 sub - 유효하지 않음")
        void subTest_invalid() {
            // Given
            long id = 1L;
            long baseAmount = 1000L;
            long updateMillis = 1L;
            long subAmount = 2000L;
            UserPoint userPoint = new UserPoint(id, baseAmount, updateMillis);

            // When
            IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> {
                userPoint.sub(subAmount);
            });

            // Then
            assertThat(result.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_POINT.getMessage());
        }
    }
}

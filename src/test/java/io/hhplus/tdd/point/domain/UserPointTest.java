package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    }


}

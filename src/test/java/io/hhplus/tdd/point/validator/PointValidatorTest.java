package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.error.PointErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointValidator 단위테스트")
class PointValidatorTest {

    private final PointValidator sut = new PointValidator();

    @Nested
    @DisplayName("id 검증")
    class validateIdTest {
        @Test
        @DisplayName("id 검증 - 유효")
        void validateIdTest_valid() {
            // given
            Long id = 1L;

            // when & then
            assertDoesNotThrow(() -> {
                sut.validateId(id);
            });
        }

        @Test
        @DisplayName("id 검증 - 유효하지 않음")
        void validateIdTest_invalid() {
            // given
            Long id = null;

            // when
            IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> {
                sut.validateId(id);
            });

            // then
            assertThat(result.getMessage()).isEqualTo(PointErrorMessage.NOT_USED_ID.getMessage());
        }
    }

    @Nested
    @DisplayName("amount 검증")
    class validateAmountTest {
        @Test
        @DisplayName("amount 검증 - 유효")
        void validateAmountTest_valid() {
            // given
            long amount = 1L;

            // when & then
            assertDoesNotThrow(() -> {
                sut.validateId(amount);
            });
        }

        @Test
        @DisplayName("amount 검증 - 유효하지 않음")
        void validateAmountTest_invalid() {
            // given
            long amount1 = 0L;
            long amount2 = -1;

            // when
            IllegalArgumentException result1 = assertThrows(IllegalArgumentException.class, () -> {
                sut.validateAmount(amount1);
            });
            IllegalArgumentException result2 = assertThrows(IllegalArgumentException.class, () -> {
                sut.validateAmount(amount2);
            });

            // then
            assertThat(result1.getMessage()).isEqualTo(PointErrorMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getMessage());
            assertThat(result2.getMessage()).isEqualTo(PointErrorMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getMessage());
        }
    }
}
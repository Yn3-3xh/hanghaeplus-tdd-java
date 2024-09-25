package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.domain.PointPolicy;
import io.hhplus.tdd.point.error.PointErrorMessage;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PointValidator {

    public void validateChargeAble(Long id, Long amount) {
        validateId(id);
        validateAmount(amount);
    }

    public void validateUseAble(Long id, Long amount) {
        validateId(id);
        validateAmount(amount);
    }

    public void validateId(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException(PointErrorMessage.NULL_ID.getMessage());
        }
    }

    public void validateAmount(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(PointErrorMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getMessage());
        }
    }

    public void validateMaxPoint(Long pointSum) {
        if (pointSum > PointPolicy.MAX_POINT.getPoint()) {
//            throw PointException.exceedMaxPoint();
        }
    }
}

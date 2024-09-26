package io.hhplus.tdd.point.error;

import io.hhplus.tdd.point.entity.PointPolicy;
import lombok.Getter;

@Getter
public enum PointErrorMessage {
    EXCEED_MAX_POINT("최대 포인트 " + PointPolicy.MAX_POINT + " 을(를) 초과할 수 없습니다."),
    NOT_USED_POINT("보유한 포인트만 사용할 수 있습니다."),
    NOT_USED_ID("요청한 ID가 null입니다."),
    AMOUNT_MUST_BE_GREATER_THAN_ZERO("충전할 포인트는 0보다 커야 합니다.");

    private final String message;

    PointErrorMessage(String message) {
        this.message = message;
    }
}

package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.error.PointErrorMessage;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public Long add(long amount) {
        long newPoint = this.point + amount;
        if (newPoint > PointPolicy.MAX_POINT.getPoint()) {
            throw new IllegalArgumentException(PointErrorMessage.EXCEED_MAX_POINT.getMessage());
        }
        return newPoint;
    }

    public Long sub(long amount) {
        long newPoint = this.point - amount;
        if (newPoint < 0) {
            throw new IllegalArgumentException(PointErrorMessage.NOT_USED_POINT.getMessage());
        }
        return newPoint;
    }
}

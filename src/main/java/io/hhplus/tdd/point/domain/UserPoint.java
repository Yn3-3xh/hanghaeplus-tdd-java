package io.hhplus.tdd.point.domain;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public Long add(long amount) {
        return this.point + amount;
    }

    public Long sub(long amount) {
        return this.point - amount;
    }
}

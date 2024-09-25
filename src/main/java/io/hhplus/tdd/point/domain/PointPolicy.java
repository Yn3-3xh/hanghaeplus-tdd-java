package io.hhplus.tdd.point.domain;

import lombok.Getter;

@Getter
public enum PointPolicy {

    MAX_POINT(10000L);

    private final Long point;

    PointPolicy(Long point) {
        this.point = point;
    }
}

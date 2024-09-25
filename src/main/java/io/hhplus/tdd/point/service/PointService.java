package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;

import java.util.List;

public interface PointService {
    UserPoint select(Long id);

    List<PointHistory> history(Long id);

    UserPoint charge(Long id, Long amount);

    UserPoint use(Long id, Long amount);
}

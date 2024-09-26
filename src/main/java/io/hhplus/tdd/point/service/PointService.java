package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PointService {
    UserPoint select(Long id);

    List<PointHistory> history(Long id);

    CompletableFuture<UserPoint> charge(Long id, Long amount);

    CompletableFuture<UserPoint> use(Long id, Long amount);
}

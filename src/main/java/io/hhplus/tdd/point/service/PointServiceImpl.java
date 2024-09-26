package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.validator.PointValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final PointValidator pointValidator;
    private final ReentrantLock lock;


    @Override
    public UserPoint select(Long id) {
        pointValidator.validateId(id);
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> history(Long id) {
        pointValidator.validateId(id);
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public CompletableFuture<UserPoint> charge(Long id, Long amount) {
        return CompletableFuture.supplyAsync(() -> {
            lock.lock();
            try {
                pointValidator.validateChargeAble(id, amount);

                UserPoint userPoint = userPointTable.selectById(id);
                long pointSum = userPoint.add(amount);

                UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, pointSum);
                pointHistoryTable.insert(id, amount, TransactionType.CHARGE, savedUserPoint.updateMillis());
                return savedUserPoint;
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    public CompletableFuture<UserPoint> use(Long id, Long amount) {
        return CompletableFuture.supplyAsync(() -> {
            lock.lock();
            try {
                pointValidator.validateUseAble(id, amount);

                UserPoint userPoint = userPointTable.selectById(id);
                long pointSum = userPoint.sub(amount);

                UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, pointSum);
                pointHistoryTable.insert(id, amount, TransactionType.USE, savedUserPoint.updateMillis());
                return savedUserPoint;
            } finally {
                lock.unlock();
            }
        });
    }
}
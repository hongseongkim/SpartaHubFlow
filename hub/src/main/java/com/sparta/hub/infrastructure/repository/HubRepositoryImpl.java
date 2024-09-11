package com.sparta.hub.infrastructure.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.domain.model.QHub;

import com.sparta.hub.domain.repository.HubCustomRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class HubRepositoryImpl implements HubCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countByIsDeletedFalse() {
        QHub hub = QHub.hub;

        return queryFactory
                .selectFrom(hub)
                .where(hub.isDeleted.eq(false))
                .fetch().size();
    }

    @Override
    public Hub findByIdAndIsDeletedFalse(UUID id) {
        QHub hub = QHub.hub;
        return queryFactory.selectFrom(hub)
                .where(hub.hubId.eq(id)
                        .and(hub.isDeleted.eq(false)))
                .fetchOne();
    }

    @Override
    public Page<Hub> findAllByIsDeletedFalse(Pageable pageable) {
        QHub hub = QHub.hub;

        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable);

        List<Hub> results = queryFactory.selectFrom(hub)
                .where(hub.isDeleted.eq(false))
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(hub)
                .where(hub.isDeleted.eq(false))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Hub> searchByName(String name, Pageable pageable) {

        QHub hub = QHub.hub;

        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable);

        List<Hub> results = queryFactory.selectFrom(hub)
                .where(hub.name.containsIgnoreCase(name)
                        .and(hub.isDeleted.eq(false)))
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(hub)
                .where(hub.name.containsIgnoreCase(name)
                        .and(hub.isDeleted.eq(false)))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    // Sort를 OrderSpecifier로 변환하는 메서드
    private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
        QHub hub = QHub.hub;

        // Querydsl에서 사용할 정렬 조건 생성
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("createdAt")) {
                    orders.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, hub.createdAt));
                } else if (order.getProperty().equals("updatedAt")) {
                    orders.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, hub.updatedAt));
                }
            });
        } else {
            // 정렬 조건이 없을 때 기본값으로 createdAt 내림차순을 적용
            orders.add(new OrderSpecifier<>(Order.DESC, hub.createdAt));
        }

        return orders;
    }
}

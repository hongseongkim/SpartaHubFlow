package com.sparta.hub.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.domain.model.QHub;

import com.sparta.hub.domain.repository.HubCustomRepository;
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

        List<Hub> results = queryFactory.selectFrom(hub)
                .where(hub.isDeleted.eq(false))
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

        List<Hub> results = queryFactory.selectFrom(hub)
                .where(hub.name.containsIgnoreCase(name)
                        .and(hub.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())


                .fetch();

        long total = queryFactory.selectFrom(hub)
                .where(hub.name.containsIgnoreCase(name)
                        .and(hub.deletedAt.isNull()))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }
}

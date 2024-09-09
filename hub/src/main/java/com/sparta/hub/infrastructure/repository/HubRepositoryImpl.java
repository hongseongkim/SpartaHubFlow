package com.sparta.hub.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.domain.model.QHub;

import com.sparta.hub.domain.repository.HubCustomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class HubRepositoryImpl implements HubCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Hub> searchByName(String name, Pageable pageable) {
        QHub hub = QHub.hub;

        List<Hub> results = queryFactory.selectFrom(hub)
                .where(hub.name.containsIgnoreCase(name)
                        .and(hub.deletedAt.isNull())) // 소프트 삭제된 항목 제외
                .offset(pageable.getOffset())   // 페이지 시작점
                .limit(pageable.getPageSize())  // 페이지 크기
                .fetch();

        long total = queryFactory.selectFrom(hub)
                .where(hub.name.containsIgnoreCase(name)
                        .and(hub.deletedAt.isNull()))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }
}

package com.sparta.hotsix.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.hotsix.user.domain.QUser;
import com.sparta.hotsix.user.domain.User;
import com.sparta.hotsix.user.dto.UserResponse;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserQueryRepository {
    private final JPAQueryFactory query;

    public UserQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }


    public Page<User> findAll(Pageable pageable) {


        QUser user = QUser.user;
        List<User> users = query.selectFrom(user)
                .where(user.isDeleted.isTrue())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc(),user.username.asc())
                .fetch();


        long total = query.selectFrom(user)
                .where(user.isDeleted.isTrue())
                .fetch().size();



        // Page<UserResponse> 객체를 생성하여 반환합니다.
        return new PageImpl<>(users, pageable, total);


    }

    public Page<User> getUserSearch(Pageable pageable,String username) {

        QUser user = QUser.user;
        List<User> users = query.selectFrom(user)
                .where(user.isDeleted.isTrue(),user.username.contains(username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc(),user.username.asc())
                .fetch();


        long total = query.selectFrom(user)
                .where(user.isDeleted.isTrue())
                .fetch().size();



        // Page<UserResponse> 객체를 생성하여 반환합니다.
        return new PageImpl<>(users, pageable, total);





    }
}

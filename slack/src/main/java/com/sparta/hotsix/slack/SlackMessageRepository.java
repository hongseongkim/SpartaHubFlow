package com.sparta.hotsix.slack;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, Long> {
}
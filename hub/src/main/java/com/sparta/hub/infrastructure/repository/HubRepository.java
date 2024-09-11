package com.sparta.hub.infrastructure.repository;

import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.domain.repository.HubCustomRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRepository extends JpaRepository<Hub, UUID>, HubCustomRepository {

}

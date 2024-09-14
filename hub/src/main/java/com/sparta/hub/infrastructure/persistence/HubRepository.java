package com.sparta.hub.infrastructure.persistence;

import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.hub.repository.HubCustomRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRepository extends JpaRepository<Hub, UUID>, HubCustomRepository {

}

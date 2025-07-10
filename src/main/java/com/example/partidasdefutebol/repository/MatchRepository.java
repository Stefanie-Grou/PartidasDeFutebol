package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
}



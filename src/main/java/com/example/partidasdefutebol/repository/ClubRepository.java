package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<ClubEntity, Long> {
}

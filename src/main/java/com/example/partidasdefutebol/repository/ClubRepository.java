package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.ClubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Long> {

    @Query("SELECT c FROM ClubEntity c WHERE "
            + "(LOWER(c.clubName) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND "
            + "(c.stateAcronym = :state OR :state IS NULL) AND "
            + "(c.isActive = :isActive OR :isActive IS NULL)")
    Page<ClubEntity> findByFilters(String name, String state, Boolean isActive, Pageable pageable);
}

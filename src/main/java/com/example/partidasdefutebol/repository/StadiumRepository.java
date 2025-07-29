package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.StadiumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<StadiumEntity, Long> {

    @Query("SELECT c FROM StadiumEntity c WHERE "
            + "(LOWER(c.stadiumName) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND "
            + "(c.stadiumState = :state OR :state IS NULL)")
    Page<StadiumEntity> findStadiumsByFilters(String name, String state, Pageable pageable);
}

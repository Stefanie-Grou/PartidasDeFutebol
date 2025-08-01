package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.Stadium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    @Query("SELECT c FROM Stadium c WHERE "
            + "(LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND "
            + "(c.stateAcronym = :state OR :state IS NULL)")
    Page<Stadium> findStadiumsByFilters(String name, String state, Pageable pageable);
}

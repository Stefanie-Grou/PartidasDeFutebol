package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.ClubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Long> {

    @Query("SELECT c FROM ClubEntity c WHERE "
            + "(LOWER(c.clubName) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND "
            + "(c.stateAcronym = :state OR :state IS NULL) AND "
            + "(c.isActive = :isActive OR :isActive IS NULL)")
    Page<ClubEntity> findByFilters(String name, String state, Boolean isActive, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(m.matchDate) = 0 THEN true ELSE false END FROM MatchEntity m " +
            "WHERE (m.awayClubId = ?2 OR m.homeClubId <= ?2) AND m.matchDate <= ?1")
    Boolean wasClubCreatedAfterGame(LocalDateTime newClubCreatedDate, Long clubId);

    @Query("SELECT CASE " +
            "WHEN (m.homeClubId = :clubId AND m.homeClubNumberOfGoals > m.awayClubNumberOfGoals) OR " +
            "(m.awayClubId = :clubId AND m.homeClubNumberOfGoals < m.awayClubNumberOfGoals) THEN 'vitória' " +
            "WHEN m.homeClubNumberOfGoals = m.awayClubNumberOfGoals THEN 'empate' " +
            "ELSE 'derrota' END AS resultado " +
            "FROM MatchEntity m " +
            "WHERE m.awayClubId = :clubId OR m.homeClubId = :clubId")
    List<String> findMatchResultsByClubId(@Param("clubId") Long clubId);

    @Query("SELECT " +
            "SUM(CASE WHEN m.homeClubId = ?1 THEN m.homeClubNumberOfGoals ELSE m.awayClubNumberOfGoals END) AS totalGolsFeitos " +
            "FROM MatchEntity m " +
            "WHERE m.homeClubId = :clubId OR m.awayClubId = :clubId")
    Integer findTotalPositiveGoalsByClubId(@Param("clubId") Long clubId);

    @Query("SELECT " +
            "SUM(CASE WHEN m.homeClubId = ?1 THEN m.awayClubNumberOfGoals ELSE m.homeClubNumberOfGoals END) AS totalGolsSofridos " +
            "FROM MatchEntity m " +
            "WHERE m.homeClubId = :clubId OR m.awayClubId = :clubId")
    Integer findTotalNegativeGoalsByClubId(@Param("clubId") Long clubId);
}

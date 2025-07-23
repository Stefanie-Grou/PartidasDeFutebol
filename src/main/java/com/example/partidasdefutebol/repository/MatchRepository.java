package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.MatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    @Query("SELECT m FROM MatchEntity m WHERE m.stadiumId = ?1 AND m.matchDate = ?2")
    List<MatchEntity> findByStadiumAndDate(Long stadiumId, LocalDateTime desiredMatchDate);

    @Query("SELECT " +
            "MAX(m.matchDate) FROM MatchEntity m " +
            "WHERE m.homeClubId = ?1 AND m.matchDate <= ?2")
    LocalDateTime hoursSinceLastGameForHomeClub(Long clubId, LocalDateTime desiredMatchDate);

    @Query("SELECT MAX(m.matchDate) FROM MatchEntity m WHERE m.awayClubId = ?1 AND m.matchDate <= ?2")
    LocalDateTime hoursSinceLastGameForAwayClub(Long clubId, LocalDateTime desiredMatchDate);

    @Query("SELECT m FROM MatchEntity m WHERE (m.homeClubId = :club OR m.homeClubId IS NOT NULL) OR " +
            "(m.awayClubId = :club OR m.awayClubId IS NOT NULL) OR (m.stadiumId = :stadium OR m.stadiumId IS NOT NULL)")
    Page<MatchEntity> findByFilters(@Param("club") Long club, @Param("stadium") Long stadium, Pageable pageable);

    @Query("SELECT \n" +
            "    h.clubName, " +
            "    a.clubName, " +
            "    m.homeClubNumberOfGoals, " +
            "    m.awayClubNumberOfGoals, " +
            "    CASE " +
            "        WHEN m.homeClubNumberOfGoals = m.awayClubNumberOfGoals THEN 'Empate' " +
            "        WHEN m.homeClubNumberOfGoals > m.awayClubNumberOfGoals THEN h.clubName " +
            "        WHEN m.homeClubNumberOfGoals < m.awayClubNumberOfGoals THEN a.clubName " +
            "    END " +
            "FROM " +
            "    MatchEntity m " +
            "JOIN " +
            "    ClubEntity h on h.id = m.homeClubId " +
            "JOIN " +
            "    ClubEntity a on a.id = m.awayClubId " +
            "WHERE " +
            "    m.homeClubId = :clubId1 AND m.awayClubId = :clubId2 " +
            "    OR (m.homeClubId = :clubId2 AND m.awayClubId = :clubId1)")
    List<Object[]> findMatchesBetweenClubs(Long clubId1, Long clubId2);
}

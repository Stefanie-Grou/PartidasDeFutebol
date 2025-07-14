package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    @Query("SELECT m FROM MatchEntity m WHERE m.stadiumId = ?1 AND m.matchDate = ?2")
    List<MatchEntity> findByStadiumAndDate(Long stadiumId, LocalDateTime desiredMatchDate);

    @Query("SELECT MAX(m.matchDate) FROM MatchEntity m WHERE m.homeClubId = ?1 AND m.matchDate <= ?2")
    LocalDateTime hoursSinceLastGameForHomeClub(Long clubId, LocalDateTime desiredMatchDate);

    @Query("SELECT MAX(m.matchDate) FROM MatchEntity m WHERE m.awayClubId = ?1 AND m.matchDate <= ?2")
    LocalDateTime hoursSinceLastGameForAwayClub(Long clubId, LocalDateTime desiredMatchDate);
}



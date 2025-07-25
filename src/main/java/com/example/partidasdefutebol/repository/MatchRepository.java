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

    @Query("SELECT m FROM MatchEntity m WHERE \n" +
            "    (:club IS NULL OR ((:showOnly = 'home' AND m.homeClubId = :club) OR " +
            "(:showOnly = 'away' AND m.awayClubId = :club) OR (:showOnly NOT IN ('home', 'away') AND (" +
            "(m.homeClubId = :club) OR m.awayClubId = :club)))) AND\n" +
            "    (:stadium IS NULL OR m.stadiumId = :stadium) AND " +
            " (:isRout = false OR (m.homeClubNumberOfGoals - m.awayClubNumberOfGoals >= 3 " +
            " OR m.awayClubNumberOfGoals - m.homeClubNumberOfGoals >= 3))")
    Page<MatchEntity> findByFilters(
            @Param("club") Long club, @Param("stadium") Long stadium, Pageable pageable,
            @Param("isRout") boolean isRout, @Param("showOnly") String showOnly);

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

    @Query("select " +
            "concat(h.clubName, ' ', m.homeClubNumberOfGoals, ' X '," +
            " a.clubName, ' ', m.awayClubNumberOfGoals) " +
            ", s.stadiumName " +
            ", DATE_FORMAT(m.matchDate, '%d-%m-%Y') " +
            "from MatchEntity m " +
            "inner join ClubEntity h on h.id = m.homeClubId " +
            "inner join ClubEntity a on a.id = m.awayClubId " +
            "inner join StadiumEntity s on s.stadiumId = m.stadiumId " +
            "where (m.homeClubNumberOfGoals - m.awayClubNumberOfGoals >= 3) " +
            "OR (m.awayClubNumberOfGoals - m.homeClubNumberOfGoals >= 3)")
    List<Object[]> getAllRouts();
}

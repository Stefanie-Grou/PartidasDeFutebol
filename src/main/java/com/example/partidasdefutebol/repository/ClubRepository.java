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
            "COALESCE(SUM(CASE WHEN m.homeClubId = ?1 THEN m.homeClubNumberOfGoals " +
            "ELSE m.awayClubNumberOfGoals END),0) AS totalGolsFeitos " +
            "FROM MatchEntity m " +
            "WHERE m.homeClubId = :clubId OR m.awayClubId = :clubId")
    Integer findTotalPositiveGoalsByClubId(@Param("clubId") Long clubId);

    @Query("SELECT " +
            "COALESCE(SUM(CASE WHEN m.homeClubId = ?1 THEN m.awayClubNumberOfGoals " +
            "ELSE m.homeClubNumberOfGoals END),0) AS totalGolsSofridos " +
            "FROM MatchEntity m " +
            "WHERE m.homeClubId = :clubId OR m.awayClubId = :clubId")
    Integer findTotalNegativeGoalsByClubId(@Param("clubId") Long clubId);

    @Query("SELECT " +
            "    CASE " +
            "        WHEN m.homeClubId = :clubId THEN v.clubName " +
            "        ELSE d.clubName " +
            "    END AS adv, " +
            "    COUNT(m) AS totalPartidas, " +
            "    SUM(CASE " +
            "        WHEN (m.homeClubId = :clubId AND m.homeClubNumberOfGoals > m.awayClubNumberOfGoals) OR " +
            "             (m.awayClubId = :clubId AND m.awayClubNumberOfGoals > m.homeClubNumberOfGoals) " +
            "        THEN 1 " +
            "        ELSE 0 " +
            "    END) AS vitoria, " +
            "    SUM(CASE " +
            "        WHEN (m.homeClubNumberOfGoals = m.awayClubNumberOfGoals) THEN 1 " +
            "        ELSE 0 " +
            "    END) AS empate, " +
            "    SUM(CASE " +
            "        WHEN (m.homeClubId = :clubId AND m.homeClubNumberOfGoals < m.awayClubNumberOfGoals) OR " +
            "             (m.awayClubId = :clubId AND m.awayClubNumberOfGoals < m.homeClubNumberOfGoals) " +
            "        THEN 1 " +
            "        ELSE 0 " +
            "    END) AS derrota, " +
            "    SUM(CASE " +
            "        WHEN m.homeClubId = :clubId THEN m.homeClubNumberOfGoals " +
            "        ELSE m.awayClubNumberOfGoals " +
            "    END) AS golsFeitos, " +
            "    SUM(CASE " +
            "        WHEN m.homeClubId = :clubId THEN m.awayClubNumberOfGoals " +
            "        ELSE m.homeClubNumberOfGoals " +
            "    END) AS golsSofridos " +
            "FROM " +
            "    MatchEntity m " +
            "JOIN " +
            "    ClubEntity d ON m.homeClubId = d.id " +
            "JOIN " +
            "    ClubEntity v ON m.awayClubId = v.id " +
            "WHERE " +
            "    m.homeClubId = :clubId OR m.awayClubId = :clubId " +
            "GROUP BY " +
            "    adv ")
    List<Object[]> findClubRetrospectiveByIdAndOpponent(@Param("clubId") Long clubId);

    @Query("""
            SELECT c.clubName AS clubName, COUNT(m) AS totalGames \
            FROM MatchEntity m \
            JOIN ClubEntity c ON c.id = m.homeClubId OR c.id = m.awayClubId \
            GROUP BY c.id
            HAVING COUNT(m) > 0
            ORDER BY totalGames DESC""")
    List<Object[]> getRankingByTotalMatches();

    @Query("""
            SELECT c.clubName, COUNT(*) AS total_wins
            FROM (
                SELECT
                    CASE
             \
                        WHEN m.homeClubNumberOfGoals > m.awayClubNumberOfGoals THEN m.homeClubId\s
                        WHEN m.homeClubNumberOfGoals < m.awayClubNumberOfGoals THEN m.awayClubId\s
                    END AS clubId
                FROM MatchEntity m\s
                WHERE m.homeClubNumberOfGoals <> m.awayClubNumberOfGoals
            ) AS winners
            INNER JOIN ClubEntity c ON c.id = winners.clubId
            GROUP BY c.clubName
            ORDER BY total_wins DESC""")
    List<Object[]> getRankingByTotalWins();

    @Query("""
            SELECT c.clubName, goalsPerClub.totalGoals FROM (
            SELECT m.homeClubId AS clubId, SUM(m.homeClubNumberOfGoals) as totalGoals
            FROM MatchEntity m\s
            GROUP BY m.homeClubId\s
            HAVING SUM(m.homeClubNumberOfGoals) > 0\s
            UNION\s
            SELECT m.awayClubId AS clubId, SUM(m.awayClubNumberOfGoals)\s
            FROM MatchEntity m\s
            GROUP BY m.awayClubId\s
            HAVING SUM(m.awayClubNumberOfGoals) > 0
            ) as goalsPerClub
            inner join ClubEntity c on c.id = goalsPerClub.clubId\s
            ORDER BY totalGoals DESC""")
    List<Object[]> getRankingByTotalGoals();

    @Query(nativeQuery = true, value = "SELECT c.club_name, clubPoints.totalPoints FROM\n" +
            "(SELECT home_club_id AS clubId, \n" +
            "SUM(CASE WHEN home_club_number_of_goals > away_club_number_of_goals THEN 3 \n" +
            "\t\t WHEN home_club_number_of_goals = away_club_number_of_goals THEN 1 \n" +
            "\t\t ELSE 0 END) AS totalPoints \n" +
            "FROM match_entity \n" +
            "GROUP BY home_club_id \n" +
            "HAVING totalPoints > 0 \n" +
            "UNION \n" +
            "SELECT away_club_id AS clubId, \n" +
            "SUM(CASE WHEN away_club_number_of_goals > home_club_number_of_goals THEN 3 \n" +
            "\t\t WHEN away_club_number_of_goals = home_club_number_of_goals THEN 1 \n" +
            "\t\t ELSE 0 END) AS totalPoints \n" +
            "FROM match_entity\n" +
            "GROUP BY away_club_id \n" +
            "HAVING totalPoints > 0) as clubPoints\n" +
            "INNER JOIN club_entity c ON c.id = clubPoints.clubId\n" +
            "ORDER BY clubPoints.totalPoints DESC")
    List<Object[]> getRankingByTotalPoints();
}

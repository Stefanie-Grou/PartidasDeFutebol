package com.example.partidasdefutebol.tests.matches;

import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.service.ClubService;
import com.example.partidasdefutebol.service.StadiumService;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest
public class matchesServiceTest {

    @Autowired
    private com.example.partidasdefutebol.service.MatchService matchService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private StadiumService stadiumService;

    @Test
    public void shouldThrowException_BothClubsAreTheSame() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            MatchEntity match = new MatchEntity();
            match.setHomeClubId(1L);
            match.setAwayClubId(1L);
            match.setHomeClubNumberOfGoals(0L);
            match.setAwayClubNumberOfGoals(0L);
            match.setStadiumId(1L);
            match.setMatchDate(LocalDateTime.now());

            matchService.isEachClubDifferent(match);
        });

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(400);
        Assertions.assertThat(exception.getMessage()).isEqualTo("Os clubes devem ser diferentes");
    }

    @Test
    public void throwsException_MatchDateIsInTheFuture() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            MatchEntity match = new MatchEntity();
            match.setHomeClubId(1L);
            match.setAwayClubId(2L);
            match.setHomeClubNumberOfGoals(0L);
            match.setAwayClubNumberOfGoals(0L);
            match.setStadiumId(1L);
            match.setMatchDate(LocalDateTime.now().plusDays(1));

            matchService.validateIfNewMatchDateIsInTheFuture(match);
        });

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(400);
        Assertions.assertThat(exception.getMessage()).isEqualTo("A data da partida não pode ser posterior ao dia atual.");
    }

    @Test
    @Transactional
    public void throwsException_InvalidMatchIdToDelete() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.deleteMatch(100L);
        });

        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    public void throwsException_WontGetMatchData_InvalidMatchId() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.getMatchById(100L);
        });

        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    public void returnsMatchInfoSuccessfully() {
        MatchEntity match = matchService.getMatchById(2L);

        assertThat(match).isNotNull();
        assertThat(match.getHomeClubId()).isEqualTo(1L);
        assertThat(match.getAwayClubId()).isEqualTo(2L);
        assertThat(match.getHomeClubNumberOfGoals()).isEqualTo(1L);
        assertThat(match.getAwayClubNumberOfGoals()).isEqualTo(1L);
        assertThat(match.getStadiumId()).isEqualTo(1L);
        assertThat(match.getMatchDate()).isNotNull();
    }
}

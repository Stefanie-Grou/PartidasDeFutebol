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

import java.time.LocalDate;
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
        MatchEntity match = matchService.getMatchById(5L);

        assertThat(match).isNotNull();
        assertThat(match.getHomeClubId()).isEqualTo(1L);
        assertThat(match.getAwayClubId()).isEqualTo(2L);
        assertThat(match.getHomeClubNumberOfGoals()).isEqualTo(3L);
        assertThat(match.getAwayClubNumberOfGoals()).isEqualTo(1L);
        assertThat(match.getStadiumId()).isEqualTo(1L);
        assertThat(match.getMatchDate()).isNotNull();
    }

    @Test
    @Transactional
    public void shouldDeleteMatchSuccessfullyAndThrowException() {
        Long matchId = 6L;
        MatchEntity matchBefore = matchService.getMatchById(matchId);
        assertThat(matchBefore).isNotNull();

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.deleteMatch(matchId);
            matchService.getMatchById(matchId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    @Transactional
    public void throwsException_InvalidMatchIdToUpdate() {
        Long matchId = 100L;
        MatchEntity matchRequestedToUpdate = new MatchEntity();
        matchRequestedToUpdate.setHomeClubId(1L);
        matchRequestedToUpdate.setAwayClubId(2L);
        matchRequestedToUpdate.setHomeClubNumberOfGoals(0L);
        matchRequestedToUpdate.setAwayClubNumberOfGoals(0L);
        matchRequestedToUpdate.setStadiumId(1L);
        matchRequestedToUpdate.setMatchDate(LocalDateTime.now());

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.updateMatch(matchId, matchRequestedToUpdate);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    public void throwsException_StadiumIsNotFreeForMatch() {
        Long matchId = 5L;
        LocalDateTime createdMatchDate = matchService.getMatchById(matchId).getMatchDate();
        Long stadiumId = matchService.getMatchById(matchId).getStadiumId();
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.stadiumIsFreeForMatchOnDay(stadiumId, createdMatchDate);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("O estádio não está livre na data desejada");
    }

    @Test
    public void throwsException_HomeClubIsResting() {
        Long matchId = 5L;
        LocalDateTime createdMatchDate = matchService.getMatchById(matchId).getMatchDate();
        Long homeClubId = matchService.getMatchById(matchId).getHomeClubId();
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.checkHomeClubRestPeriod(homeClubId, createdMatchDate);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("O descanso mínimo para o clube é de 48 horas");
    }

    @Test
    public void throwsException_AwayClubIsResting() {
        Long matchId = 5L;
        LocalDateTime createdMatchDate = matchService.getMatchById(matchId).getMatchDate();
        Long awayClubId = matchService.getMatchById(matchId).getAwayClubId();
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.checkAwayClubRestPeriod(awayClubId, createdMatchDate);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("O descanso mínimo para o clube é de 48 horas");
    }

    @Test
    public void throwsException_BothClubsAreTheSame() {
        MatchEntity match = new MatchEntity();
        match.setHomeClubId(1L);
        match.setAwayClubId(1L);
        match.setHomeClubNumberOfGoals(0L);
        match.setAwayClubNumberOfGoals(0L);
        match.setStadiumId(1L);
        match.setMatchDate(LocalDateTime.now());

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.isEachClubDifferent(match);
        });
        assertThat(exception.getStatusCode()).isEqualTo(400);
        assertThat(exception.getMessage()).isEqualTo("Os clubes devem ser diferentes");
    }
}

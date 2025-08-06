package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.Matches;
import com.example.partidasdefutebol.exceptions.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

    public Matches createsValidMatch() {
        Matches match = new Matches();
        match.setHomeClubId(1L);
        match.setAwayClubId(2L);
        match.setHomeClubNumberOfGoals(0L);
        match.setAwayClubNumberOfGoals(0L);
        match.setStadiumId(1L);
        match.setMatchDate(LocalDateTime.now().minusMonths(1));
        matchService.createMatch(match);
        return match;
    }

    @Test
    public void shouldThrowException_BothClubsAreTheSame() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            Matches match = new Matches();
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
        CustomException exception = assertThrows(CustomException.class, () -> {
            Matches match = new Matches();
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
    public void throwsException_InvalidMatchIdToDelete() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            matchService.deleteMatch(100L);
        });

        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    public void throwsException_WontGetMatchData_InvalidMatchId() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            matchService.getMatchById(100L);
        });

        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    public void returnsMatchInfoSuccessfully() {
        Matches match = createsValidMatch();

        assertThat(match.getHomeClubId()).isEqualTo(match.getHomeClubId());
        assertThat(match.getAwayClubId()).isEqualTo(match.getAwayClubId());
        assertThat(match.getHomeClubNumberOfGoals()).isEqualTo(match.getHomeClubNumberOfGoals());
        assertThat(match.getAwayClubNumberOfGoals()).isEqualTo(match.getAwayClubNumberOfGoals());
        assertThat(match.getStadiumId()).isEqualTo(match.getStadiumId());
    }

    @Test
    public void shouldNotDeleteMatchSuccessfullyAndThrowException() {
        Long matchId = 6L;
        Matches matchBefore = matchService.getMatchById(matchId);
        assertThat(matchBefore).isNotNull();

        CustomException exception = assertThrows(CustomException.class, () -> {
            matchService.deleteMatch(matchId);
            matchService.getMatchById(matchId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("A partida não existe na base de dados.");
    }

    @Test
    public void throwsException_InvalidMatchIdToUpdate() {
        Long matchId = 100L;
        Matches matchRequestedToUpdate = new Matches();
        matchRequestedToUpdate.setHomeClubId(1L);
        matchRequestedToUpdate.setAwayClubId(2L);
        matchRequestedToUpdate.setHomeClubNumberOfGoals(0L);
        matchRequestedToUpdate.setAwayClubNumberOfGoals(0L);
        matchRequestedToUpdate.setStadiumId(1L);
        matchRequestedToUpdate.setMatchDate(LocalDateTime.now());

        CustomException exception = assertThrows(CustomException.class, () -> {
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
        CustomException exception = assertThrows(CustomException.class, () -> {
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
        CustomException exception = assertThrows(CustomException.class, () -> {
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
        CustomException exception = assertThrows(CustomException.class, () -> {
            matchService.checkAwayClubRestPeriod(awayClubId, createdMatchDate);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("O descanso mínimo para o clube é de 48 horas");
    }

    @Test
    public void throwsException_BothClubsAreTheSame() {
        Matches match = new Matches();
        match.setHomeClubId(1L);
        match.setAwayClubId(1L);
        match.setHomeClubNumberOfGoals(0L);
        match.setAwayClubNumberOfGoals(0L);
        match.setStadiumId(1L);
        match.setMatchDate(LocalDateTime.now());

        CustomException exception = assertThrows(CustomException.class, () -> {
            matchService.isEachClubDifferent(match);
        });
        assertThat(exception.getStatusCode()).isEqualTo(400);
        assertThat(exception.getMessage()).isEqualTo("Os clubes devem ser diferentes");
    }
}

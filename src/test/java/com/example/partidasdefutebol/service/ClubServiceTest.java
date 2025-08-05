package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.Club;
import com.example.partidasdefutebol.entities.Matches;
import com.example.partidasdefutebol.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest
public class ClubServiceTest {
    @Autowired
    private com.example.partidasdefutebol.service.ClubService clubService;

    @Autowired
    private com.example.partidasdefutebol.service.MatchService matchService;

    public Club createsValidClub() {
        Club clubEntity = new Club();
        clubEntity.setName("Coritiba");
        clubEntity.setStateAcronym("PR");
        clubEntity.setCreatedOn(LocalDate.of(1990, 1, 1));
        clubEntity.setIsActive(true);
        clubService.createClub(clubEntity);
        return clubEntity;
    }

    public Club createsAnotherValidClub() {
        Club clubEntity = new Club();
        clubEntity.setName("Paraná");
        clubEntity.setStateAcronym("PR");
        clubEntity.setCreatedOn(LocalDate.of(1900, 1, 1));
        clubEntity.setIsActive(true);
        clubService.createClub(clubEntity);
        return clubEntity;
    }

    @Test
    public void shouldCreateClubEntitySucessfully() {
        Club clubEntity = createsValidClub();
        assertThat(clubEntity.getName()).isEqualTo("Coritiba");
        assertThat(clubEntity.getStateAcronym()).isEqualTo("PR");
        assertThat(clubEntity.getCreatedOn()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(clubEntity.getIsActive()).isTrue();
    }

    @Test
    public void shouldThrowExceptionWontFindClub_InvalidClubId() {
        Long clubId = 190L;
        Exception exception = assertThrows(Exception.class, () -> {
            clubService.doesClubExist(clubId);
        });
        assertThat(exception.getMessage()).isEqualTo("Clube " + clubId + " não encontrado na base de dados.");
    }

    @Test
    public void shoulUpdateClubSucessfully_ValidInformation() {
        //Creation
        Club clubBeforeUpdate = new Club();
        clubBeforeUpdate.setName("Pinheiros");
        clubBeforeUpdate.setStateAcronym("PR");
        clubBeforeUpdate.setCreatedOn(LocalDate.of(1997, 1, 1));
        clubBeforeUpdate.setIsActive(true);
        clubService.createClub(clubBeforeUpdate);
        assertThat(clubBeforeUpdate.getName()).isEqualTo("Pinheiros");
        assertThat(clubBeforeUpdate.getStateAcronym()).isEqualTo("PR");
        assertThat(clubBeforeUpdate.getCreatedOn()).isEqualTo(LocalDate.of(1997, 1, 1));
        assertThat(clubBeforeUpdate.getIsActive()).isTrue();

        //Update
        Club clubToBeUpdated = createsValidClub();

        assertThat(clubToBeUpdated.getName()).isEqualTo("Coritiba");
        assertThat(clubToBeUpdated.getStateAcronym()).isEqualTo("PR");
        assertThat(clubToBeUpdated.getCreatedOn()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(clubToBeUpdated.getIsActive()).isTrue();
    }

    @Test
    public void shouldDeleteClubSucessfully() {
        Club clubToBeDeleted = createsValidClub();
        clubService.deleteClub(clubToBeDeleted.getId());
        assertThat(clubService.findClubById(clubToBeDeleted.getId()).getIsActive()).isFalse();
    }

    @Test
    public void shouldFindAndReturnClubEntity_ValidId() {
        Club validClub = createsValidClub();
        Club returnedClub = clubService.findClubById(validClub.getId());
        assertThat(returnedClub).isNotNull();
        assertThat(returnedClub.getIsActive()).isTrue();
        assertThat(returnedClub.getName()).isEqualTo("Coritiba");
        assertThat(returnedClub.getStateAcronym()).isEqualTo("PR");
        assertThat(returnedClub.getCreatedOn()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void shouldNotFindClubAndThrowException_InvalidClubId() {
        Long clubId = 190L;
        Exception exception = assertThrows(AmqpRejectAndDontRequeueException.class, () -> {
            clubService.findClubById(clubId);
        });
        assertThat(exception.getMessage()).isEqualTo("Clube " + clubId + " não encontrado na base de dados.");
    }

    @Test
    public void shouldThrowException_InactiveClubEntityOnDB() {
        Club deletedClub = createsValidClub();
        deletedClub.setIsActive(false);
        assertThat(deletedClub.getIsActive()).isFalse();

        CustomException exception = assertThrows(CustomException.class, () -> {
            clubService.isClubInactive(deletedClub);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        String expectedExceptionMessage = "O clube " + deletedClub.getName() + " está inativo";
        assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
    }

    @Test
    public void throwsException_ClubCreationIsPastMatchDate() {
        Club homeClub = createsValidClub(); //creation date: 1990-01-01
        Club awayClub = createsAnotherValidClub();
        //create match
        Matches match = new Matches();
        match.setHomeClubId(homeClub.getId());
        match.setAwayClubId(awayClub.getId());
        match.setHomeClubNumberOfGoals(0L);
        match.setAwayClubNumberOfGoals(0L);
        match.setStadiumId(1L);
        match.setMatchDate(homeClub.getCreatedOn().minusWeeks(4).atStartOfDay());

        AmqpRejectAndDontRequeueException exception = assertThrows(
                AmqpRejectAndDontRequeueException.class, () ->
                        matchService.createMatch(match) //creation date: 1990-01-01(match)
        );
        assertThat(exception.getMessage()).isEqualTo("A data de criação do clube " + awayClub.getName() +
                " deve ser anterior ao registro de alguma partida cadastrada.");
    }
}

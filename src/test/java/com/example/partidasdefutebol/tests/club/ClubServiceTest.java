package com.example.partidasdefutebol.tests.club;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
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

    @Test
    @Transactional
    public void shouldCreateClubEntitySucessfully() {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("Coritiba");
        clubEntity.setStateAcronym("PR");
        clubEntity.setCreatedOn(LocalDate.of(1990, 1, 1));
        clubEntity.setIsActive(true);

        assertThat(clubEntity.getClubName()).isEqualTo("Coritiba");
        assertThat(clubEntity.getStateAcronym()).isEqualTo("PR");
        assertThat(clubEntity.getCreatedOn()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(clubEntity.getIsActive()).isTrue();
    }

    @Test
    public void shouldThrowExceptionWontFindClub_InvalidClubId() {
        Long clubId = 190L;
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            clubService.doesClubExist(clubId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("Clube não encontrado na base de dados.");
    }

    @Test
    @Transactional
    public void shoulUpdateClubSucessfully_ValidInformation() {
        Long clubToUpdateId = 43L;
        ClubEntity clubToBeUpdatedEntity = new ClubEntity();
        clubToBeUpdatedEntity.setClubName("Coritiba");
        clubToBeUpdatedEntity.setStateAcronym("PR");
        clubToBeUpdatedEntity.setCreatedOn(LocalDate.of(1990, 1, 1));
        clubToBeUpdatedEntity.setIsActive(true);
        clubService.updateClub(clubToUpdateId, clubToBeUpdatedEntity);

        assertThat(clubToBeUpdatedEntity.getClubName()).isEqualTo("Coritiba");
        assertThat(clubToBeUpdatedEntity.getStateAcronym()).isEqualTo("PR");
        assertThat(clubToBeUpdatedEntity.getCreatedOn()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(clubToBeUpdatedEntity.getIsActive()).isTrue();
    }

    @Test
    @Transactional
    public void shouldDeleteClubSucessfully() {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("Vitória");
        clubEntity.setStateAcronym("PR");
        clubEntity.setCreatedOn(LocalDate.of(1990, 1, 1));
        clubEntity.setIsActive(true);
        clubService.createClub(clubEntity);
        Long clubEntityId = clubEntity.getId();

        clubService.deleteClub(clubEntityId);

        assertThat(clubEntity.getIsActive()).isFalse();
    }

    @Test
    public void shouldFindAndReturnClubEntity_ValidId() {
        Long clubId = 4L;
        ClubEntity clubEntity = clubService.findClubById(clubId);
        assertThat(clubEntity.getId()).isEqualTo(clubId);
        assertThat(clubEntity).isNotNull();
        assertThat(clubEntity.getIsActive()).isTrue();
        assertThat(clubEntity.getClubName()).isEqualTo("São Paulo");
        assertThat(clubEntity.getId()).isEqualTo(clubId);
        assertThat(clubEntity.getStateAcronym()).isEqualTo("MS");
        assertThat(clubEntity.getCreatedOn()).isEqualTo(LocalDate.of(2023, 10, 1));
    }

    @Test
    public void shouldNotFindClubAndThrowException_InvalidClubId() {
        Long clubId = 190L;
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            clubService.findClubById(clubId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("Clube não encontrado na base de dados.");
    }

    @Test
    public void shouldThrowException_InactiveClubEntityOnDB() {
        Long clubId = 3L;
        ClubEntity clubEntity = clubService.findClubById(clubId);
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            clubService.isClubInactive(clubEntity);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        String expectedExceptionMessage = "O clube " + clubEntity.getClubName() + " está inativo";
        assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
    }
}

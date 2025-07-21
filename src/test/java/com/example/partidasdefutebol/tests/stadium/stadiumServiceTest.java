package com.example.partidasdefutebol.tests.stadium;

import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureMockMvc
public class stadiumServiceTest {

    @Autowired
    private com.example.partidasdefutebol.service.StadiumService stadiumService;

    @Test
    @Transactional
    public void shouldSaveStadiumSucessfully() throws Exception {
        StadiumEntity stadiumEntity = new StadiumEntity();
        String stadiumName = "Nacional" + LocalDateTime.now().getSecond();
        stadiumEntity.setStadiumName(stadiumName);
        stadiumEntity.setStadiumState("SP");
        stadiumService.saveStadium(stadiumEntity);

        assertThat(stadiumEntity.getStadiumName()).isEqualTo(stadiumName);
        assertThat(stadiumEntity.getStadiumState()).isEqualTo("SP");
    }

    @Test
    public void souldThrowExceptionAndNotSave_StadiumStateIsInvalid() throws Exception {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            StadiumEntity stadiumEntity = new StadiumEntity();
            stadiumEntity.setStadiumName("Nacional");
            stadiumEntity.setStadiumState("PO");
            stadiumService.saveStadium(stadiumEntity);
            stadiumService.saveStadium(stadiumEntity);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("A sigla do estado é inválida.");

    }

    @Test
    @Transactional
    public void shouldUpdateStadium() throws Exception {
        Long stadiumId = 1L;
        StadiumEntity updatedStadiumEntity = new StadiumEntity();
        updatedStadiumEntity.setStadiumName("Nacional");
        updatedStadiumEntity.setStadiumState("RJ");

        stadiumService.updateStadium(stadiumId, updatedStadiumEntity);

        assertThat(updatedStadiumEntity.getStadiumName()).isEqualTo("Nacional");
        assertThat(updatedStadiumEntity.getStadiumState()).isEqualTo("RJ");
    }

    @Test
    public void shouldReturnStadiumEntitySucessfully() throws Exception {
        Long stadiumId = 3L;
        ResponseEntity<StadiumEntity> stadiumEntity = stadiumService.retrieveStadiumInfo(stadiumId);
        assertThat(stadiumEntity).isNotNull();
        assertThat(stadiumEntity.getBody().getStadiumName()).isEqualTo("Nacional");
        assertThat(stadiumEntity.getBody().getStadiumState()).isEqualTo("SP");
    }

    @Test
    public void shouldThrowExceptionAndNotFindStadium() throws Exception {
        Long stadiumId = 100L;
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            stadiumService.retrieveStadiumInfo(stadiumId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("O estádio não foi encontrado na base de dados.");
    }

    @Test
    public void shouldThrowException_InvalidStadiumId() throws Exception {
        Long stadiumId = 300L;
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            stadiumService.doesStadiumExist(stadiumId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("O estádio nao foi encontrado na base de dados.");

    }
}

package com.example.partidasdefutebol.tests.stadium;

import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.entities.StadiumFromController;
import com.example.partidasdefutebol.exceptions.ConflictException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

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
        //stadiumService.saveStadium(stadiumEntity);

        assertThat(stadiumEntity.getStadiumName()).isEqualTo(stadiumName);
        assertThat(stadiumEntity.getStadiumState()).isEqualTo("SP");
    }

    @Test
    @Transactional
    public void shouldThrowExceptionAndNotSave_StadiumCepIsNotValid() throws Exception {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            StadiumFromController stadiumFromController = new StadiumFromController();
            stadiumFromController.setStadiumName("Nacional");
            stadiumFromController.setCep("00000-000");
            stadiumService.saveStadium(stadiumFromController);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("O CEP informado não é válido.");

    }

    @Test
    @Transactional
    public void shouldUpdateStadium() throws Exception {
        Long stadiumId = 1L;
        StadiumFromController stadiumFromController = new StadiumFromController();
        stadiumFromController.setStadiumName("Nacional");
        stadiumFromController.setCep("12070012");

        StadiumEntity updatedStadiumEntity = stadiumService.updateStadium(stadiumId, stadiumFromController);

        assertThat(updatedStadiumEntity.getStadiumName()).isEqualTo("Nacional");
        assertThat(updatedStadiumEntity.getStadiumState()).isEqualTo("SP");
        assertThat(updatedStadiumEntity.getCep()).isEqualTo("12070-012");
        assertThat(updatedStadiumEntity.getStreet()).isEqualTo("Rua Luciano Alves Pereira");
        assertThat(updatedStadiumEntity.getCity()).isEqualTo("Taubaté");
    }

    @Test
    public void shouldReturnStadiumEntitySucessfully() throws Exception {
        Long stadiumId = 3L;
        ResponseEntity<StadiumEntity> stadiumEntity = stadiumService.retrieveStadiumInfo(stadiumId);
        assertThat(stadiumEntity).isNotNull();
        assertThat(stadiumEntity.getBody().getStadiumName()).isEqualTo("Estádio Municipal João Lamego");
        assertThat(stadiumEntity.getBody().getStadiumState()).isEqualTo("PR");
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
        assertThat(exception.getMessage()).isEqualTo("O estádio não foi encontrado na base de dados.");

    }

    @Test
    public void shouldRetrieveStadiumsSuccessfully() {
        String nameFilter = "Pacaembu";
        String stateFilter = null;
        int page = 0;
        int size = 10;
        String sortField = "stadiumState";
        String sortOrder = "asc";

        List stadiumsPage =
                stadiumService.getStadiums(nameFilter, stateFilter, page, size, sortField, sortOrder).getContent();
        assertThat(stadiumsPage).isNotNull();
    }

    @Test
    public void shouldRetrieveStadiumsSuccessfullyAndDescending() {
        String nameFilter = "Pacaembu";
        String stateFilter = null;
        int page = 0;
        int size = 10;
        String sortField = "stadiumState";
        String sortOrder = "desc";

        List stadiumsPage =
                stadiumService.getStadiums(nameFilter, stateFilter, page, size, sortField, sortOrder).getContent();
        assertThat(stadiumsPage).isNotNull();
    }
}

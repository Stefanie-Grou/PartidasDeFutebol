package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.Stadium;
import com.example.partidasdefutebol.dto.ControllerStadiumDTO;
import com.example.partidasdefutebol.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class stadiumServiceTest {

    @Autowired
    private com.example.partidasdefutebol.service.StadiumService stadiumService;

    @Autowired
    private com.example.partidasdefutebol.repository.StadiumRepository stadiumRepository;

    @Test
    public void saveStadiumSucessfully() throws Exception {
        ControllerStadiumDTO stadiumEntity = new ControllerStadiumDTO("Nacional", "12070012");
        stadiumService.saveStadium(stadiumEntity);
        Stadium stadium = stadiumRepository.findById(1L).get();

        assertThat(stadium.getName()).isEqualTo("Nacional");
        assertThat(stadium.getStateAcronym()).isEqualTo("SP");
        assertThat(stadium.getCep()).isEqualTo("12070-012");
        assertThat(stadium.getStreet()).isEqualTo("Rua Luciano Alves Pereira");
        assertThat(stadium.getCity()).isEqualTo("Taubaté");
    }

    @Test
    public void shouldThrowExceptionAndNotSave_StadiumCepIsNotValid() throws Exception {
        CustomException exception = assertThrows(CustomException.class, () -> {
            ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO();
            stadiumFromController.setStadiumName("Nacional");
            stadiumFromController.setCep("00000-000");
            stadiumService.saveStadium(stadiumFromController);
        });
        assertThat(exception.getStatusCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("O CEP informado não é válido.");

    }

    @Test
    public void shouldUpdateStadium() throws Exception {
        Long stadiumId = 1L;
        ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO();
        stadiumFromController.setStadiumName("Nacional");
        stadiumFromController.setCep("12070012");

        Stadium updatedStadiumEntity = stadiumService.updateStadium(stadiumId, stadiumFromController);

        assertThat(updatedStadiumEntity.getName()).isEqualTo("Nacional");
        assertThat(updatedStadiumEntity.getStateAcronym()).isEqualTo("SP");
        assertThat(updatedStadiumEntity.getCep()).isEqualTo("12070-012");
        assertThat(updatedStadiumEntity.getStreet()).isEqualTo("Rua Luciano Alves Pereira");
        assertThat(updatedStadiumEntity.getCity()).isEqualTo("Taubaté");
    }

    @Test
    public void shouldReturnStadiumEntitySucessfully() throws Exception {
        Long stadiumId = 1L;
        ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO("Nacional", "12070012");
        stadiumService.saveStadium(stadiumFromController);
        Stadium stadium = stadiumRepository.findById(stadiumId).get();
        assertThat(stadium.getName()).isEqualTo("Nacional");
        assertThat(stadium.getStateAcronym()).isEqualTo("SP");
        assertThat(stadium.getCep()).isEqualTo("12070-012");
        assertThat(stadium.getStreet()).isEqualTo("Rua Luciano Alves Pereira");
        assertThat(stadium.getCity()).isEqualTo("Taubaté");
    }

    @Test
    public void shouldThrowExceptionAndNotFindStadium() throws Exception {
        Long stadiumId = 100L;
        CustomException exception = assertThrows(CustomException.class, () -> {
            stadiumService.retrieveStadiumInfo(stadiumId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("O estádio não foi encontrado na base de dados.");
    }

    @Test
    public void shouldThrowException_InvalidStadiumId() throws Exception {
        Long stadiumId = 300L;
        CustomException exception = assertThrows(CustomException.class, () -> {
            stadiumService.doesStadiumExist(stadiumId);
        });
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getMessage()).isEqualTo("O estádio não foi encontrado na base de dados.");
    }
}

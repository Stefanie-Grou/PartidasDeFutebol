package com.example.partidasdefutebol.tests.stadium;

import com.example.partidasdefutebol.controller.StadiumController;
import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.repository.StadiumRepository;
import com.example.partidasdefutebol.service.StadiumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class stadiumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StadiumService stadiumService;

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumController stadiumController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCreateStadiumSucessfully() throws Exception {
        StadiumEntity stadiumEntity = new StadiumEntity();
        String stadiumName = "Nacional" + LocalDateTime.now().getSecond();
        stadiumEntity.setStadiumName(stadiumName);
        stadiumEntity.setStadiumState("AC");

        MvcResult mvcResult = mockMvc.perform(post("/estadio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        assertThat(mvcResult.getResponse().getContentAsString()).contains(stadiumName);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("AC");
    }

    @Test
    public void shouldThrowException_InvalidStadiumStateAcronym() throws Exception {
        StadiumEntity stadiumEntity = new StadiumEntity();
        String stadiumName = "Nacional" + LocalDateTime.now().getSecond();
        stadiumEntity.setStadiumName(stadiumName);
        stadiumEntity.setStadiumState("PQ");
        MvcResult mvcResult = mockMvc.perform(post("/estadio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumEntity)))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(409);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("A sigla do estado é inválida.");
    }

    @Test
    @Transactional
    public void shoulduUpdateStadiumSucessfully() throws Exception {
        Long stadiumId = 2L;
        StadiumEntity stadiumEntity = new StadiumEntity();
        stadiumEntity.setStadiumName("Pacaembu");
        stadiumEntity.setStadiumState("AC");
        MvcResult mvcResult = mockMvc.perform(put("/estadio/{id}", stadiumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Pacaembu");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("AC");
    }

    @Test
    public void shouldThrowException_InvalidStadiumId() throws Exception {
        Long stadiumId = 100L;
        StadiumEntity stadiumEntity = new StadiumEntity();
        stadiumEntity.setStadiumName("Pacaembu");
        stadiumEntity.setStadiumState("AC");
        MvcResult mvcResult = mockMvc.perform(put("/estadio/{id}", stadiumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumEntity)))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O estádio nao foi encontrado na base de dados.");
    }

    @Test
    public void shouldGetStadiumInfoSuccessfully() throws Exception {
        Long stadiumId = 1L;
        MvcResult mvcResult = mockMvc.perform(get("/estadio/{id}", stadiumId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Nacional");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("RJ");
    }

    @Test
    public void shouldThrowExceptionAndWontGetStadium_InvalidStadiumId() throws Exception {
        Long stadiumId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/estadio/{id}", stadiumId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O estádio não foi encontrado na base de dados.");
    }
}

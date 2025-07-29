package com.example.partidasdefutebol.tests.club;

import com.example.partidasdefutebol.controller.ClubController;
import com.example.partidasdefutebol.entities.GoalSummary;
import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.service.ClubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ClubService clubeService;

    @InjectMocks
    private ClubController clubeController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private ClubService clubService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    public void shouldCreateClub() throws Exception {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("Coritiba");
        clubEntity.setStateAcronym("AC");
        clubEntity.setCreatedOn(LocalDate.of(2023, 1, 1));
        clubEntity.setIsActive(true);

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Coritiba");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("AC");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("2023-01-01");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("true");
    }

    @Test
    public void shouldNotCreateClub_ClubNameIsTooShort() throws Exception {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("A");
        clubEntity.setStateAcronym("AC");
        clubEntity.setCreatedOn(LocalDate.of(2023, 1, 1));
        clubEntity.setIsActive(true);

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O nome do clube deve ser de, no mínimo, duas letras.");
    }

    @Test
    public void shouldNotCreateClub_StateAcronymIsInvalid() throws Exception {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("Coritiba");
        clubEntity.setStateAcronym("AJ");
        clubEntity.setCreatedOn(LocalDate.of(2023, 1, 1));
        clubEntity.setIsActive(true);

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(409);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("A sigla do estado é inválida.");
    }

    @Test
    @Transactional
    public void shouldNotCreateClub_DataIntegrityViolation() throws Exception {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("Coritiba");
        clubEntity.setStateAcronym("MT");
        clubEntity.setCreatedOn(LocalDate.of(2023, 1, 1));
        clubEntity.setIsActive(true);

        mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        MvcResult dataIntegrityViolation = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(dataIntegrityViolation.getResponse().getStatus()).isEqualTo(409);
        assertThat(dataIntegrityViolation.getResponse().getContentAsString())
                .contains("Já existe um registro de mesmo nome para este estado.");
    }

    @Test
    public void shoulNotCreateClub_DateIsInTheFuture() throws Exception {
        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setClubName("Coritiba");
        clubEntity.setStateAcronym("AC");
        clubEntity.setCreatedOn(LocalDate.of(2027, 1, 1));
        clubEntity.setIsActive(true);

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("A data de criação do clube deve ser presente ou passada.");
    }

    @Test
    @Transactional
    public void shouldUpdateClub_AllValidData() throws Exception {
        Long clubId = 8L;

        ClubEntity updatedClubEntity = new ClubEntity();
        updatedClubEntity.setClubName("Palmeiras");
        updatedClubEntity.setStateAcronym("SP");
        updatedClubEntity.setCreatedOn(LocalDate.of(1990, 1, 1));
        updatedClubEntity.setIsActive(true);

        MvcResult mvcResult = mockMvc.perform(put("/clube/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Palmeiras");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("SP");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("1990-01-01");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("true");
    }

    @Test
    public void shouldNotUpdateClub_InvalidId() throws Exception {
        Long clubId = 100L;
        ClubEntity updatedClubEntity = new ClubEntity();
        updatedClubEntity.setClubName("Palmeiras");
        updatedClubEntity.setStateAcronym("SP");
        updatedClubEntity.setCreatedOn(LocalDate.of(2024, 1, 1));
        updatedClubEntity.setIsActive(true);
        MvcResult mvcResult = mockMvc.perform(put("/clube/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Clube " + clubId + " não encontrado na base de dados.");
    }

    @Test
    public void shouldNotUpdateClub_ClubNameIsInvalid() throws Exception {
        Long clubId = 80L;
        ClubEntity updatedClubEntity = new ClubEntity();
        updatedClubEntity.setClubName("P");
        updatedClubEntity.setStateAcronym("SP");
        updatedClubEntity.setCreatedOn(LocalDate.of(2024, 1, 1));
        updatedClubEntity.setIsActive(true);
        MvcResult mvcResult = mockMvc.perform(put("/clube/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O nome do clube deve ser de, no mínimo, duas letras.");
    }

    @Test
    @Transactional
    public void shouldDeleteClub() throws Exception {
        Long clubId = 8L;
        MvcResult mvcResult = mockMvc.perform(delete("/clube/{id}", clubId))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }

    @Test
    @Transactional
    public void shouldNotDeleteClub_InvalidId() throws Exception {
        Long clubId = 100L;
        MvcResult mvcResult = mockMvc.perform(delete("/clube/{id}", clubId))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldGetOneClubById() throws Exception {
        Long clubId = 8L;
        MvcResult mvcResult = mockMvc.perform(get("/clube/{id}", clubId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Tigre");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("RS");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("1966-07-12");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("true");
    }

    @Test
    public void shouldNotGetOneClub_InvalidId() throws Exception {
        Long clubId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/clube/{id}", clubId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldGetAllClubs() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/clube"))
                .andReturn();
        //These aren't everything I'm expecting as a response, but it's enough for now
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Novorizontino");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Novorizontino");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Novorizontino");
    }

    @Test
    public void shouldGetAllClubsByName() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/clube?name=Tigre"))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Tigre");
        assertThat(response).contains("RS");
        assertThat(response).contains("1966");
    }

    @Test
    public void shouldRetrieveClubRetrospectiveSucessfully() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/clube/retrospecto/1"))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        GoalSummary goalSummary =
                new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), GoalSummary.class);
        assertThat(goalSummary.getTotalOfPositiveGoals()).isEqualTo(2);
        assertThat(goalSummary.getTotalOfNegativeGoals()).isEqualTo(0);
        assertThat(goalSummary.getTotalOfVictories()).isEqualTo(2);
        assertThat(goalSummary.getTotalOfDraws()).isEqualTo(0);
        assertThat(goalSummary.getTotalOfDefeats()).isEqualTo(0);
    }

    @Test
    public void shouldNotRetrieveClubRetrospective_InvalidId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/clube/retrospecto/100"))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Clube 100 não encontrado na base de dados.");
    }
}
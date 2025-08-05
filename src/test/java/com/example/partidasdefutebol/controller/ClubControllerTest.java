package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.dto.GoalSummaryDTO;
import com.example.partidasdefutebol.entities.Club;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public Club createsValidClub() {
        Club clubEntity = new Club();
        clubEntity.setName("Coritiba");
        clubEntity.setStateAcronym("PR");
        clubEntity.setCreatedOn(LocalDate.of(1990, 1, 1));
        clubEntity.setIsActive(true);
        clubService.createClub(clubEntity);
        return clubEntity;
    }

    @Test
    public void shouldCreateClub() throws Exception {
        Club clubEntity = createsValidClub();

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(202);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Aguardando processamento");
        Club createdClub = clubService.findClubById(clubEntity.getId());
        assertThat(createdClub.getName()).isEqualTo(clubEntity.getName());
        assertThat(createdClub.getStateAcronym()).isEqualTo(clubEntity.getStateAcronym());
        assertThat(createdClub.getCreatedOn()).isEqualTo(clubEntity.getCreatedOn());
        assertThat(createdClub.getIsActive()).isEqualTo(clubEntity.getIsActive());
    }

    @Test
    public void shouldNotCreateClub_ClubNameIsTooShort() throws Exception {
        Club clubEntity = createsValidClub();
        clubEntity.setName("C");

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O nome do clube deve ser de, no mínimo, duas letras.");
    }

    @Test
    public void shouldNotCreateClub_StateAcronymIsInvalid() throws Exception {
        Club clubEntity = new Club
                (1L,"Coritiba", "AJ", LocalDate.of(1990, 1, 1), true);

        MvcResult mvcResult = mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O estado AJ é inválido.");
    }

    @Test
    public void shoulNotCreateClub_DateIsInTheFuture() throws Exception {
        Club clubEntity = new Club();
        clubEntity.setName("Coritiba");
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
    public void shouldUpdateClub_AllValidData() throws Exception {
        Long clubId = 1L;
        //Before update
        Club clubToBeUpdated = new Club();
        clubToBeUpdated.setName("Palmeiras");
        clubToBeUpdated.setStateAcronym("SP");
        clubToBeUpdated.setCreatedOn(LocalDate.of(1990, 1, 1));
        clubToBeUpdated.setIsActive(true);
        clubService.createClub(clubToBeUpdated);
        assertThat(clubService.findClubById(clubId).getName()).isEqualTo("Palmeiras");
        assertThat(clubService.findClubById(clubId).getStateAcronym()).isEqualTo("SP");
        assertThat(clubService.findClubById(clubId).getCreatedOn()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(clubService.findClubById(clubId).getIsActive()).isEqualTo(true);

        //Update
        MvcResult mvcResult = mockMvc.perform(put("/clube/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubToBeUpdated)))
                .andReturn();


        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(202);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Aguardando processamento");
    }

    @Test
    public void throwsException_InvalidClubId() throws Exception {
        Long clubId = 100000L;
        Club updatedClubEntity = createsValidClub();
        MvcResult mvcResult = mockMvc.perform(put("/clube/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClubEntity)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Clube " + clubId + " não encontrado na base de dados.");
    }

    @Test
    public void shouldNotUpdateClub_ClubNameIsInvalid() throws Exception {
        Long clubId = 8L;
        Club updatedClubEntity = new Club();
        updatedClubEntity.setName("P");
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
    public void shouldDeleteClub() throws Exception {
        Club clubEntity = createsValidClub();
        MvcResult mvcResult = mockMvc.perform(delete("/clube/{id}", clubEntity.getId()))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
        assertThat(clubService.findClubById(clubEntity.getId()).getIsActive()).isEqualTo(false);
    }

    @Test
    public void shouldNotDeleteClub_InvalidId() throws Exception {
        Long clubId = 100L;
        MvcResult mvcResult = mockMvc.perform(delete("/clube/{id}", clubId))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Clube " + clubId + " não encontrado na base de dados.");
    }

    @Test
    public void shouldGetOneClubById() throws Exception {
        Club clubEntity = createsValidClub();
        MvcResult mvcResult = mockMvc.perform(get("/clube/{id}", clubEntity.getId()))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains(clubEntity.getName());
        assertThat(mvcResult.getResponse().getContentAsString()).contains(clubEntity.getStateAcronym());
        assertThat(mvcResult.getResponse().getContentAsString()).contains(clubEntity.getCreatedOn().toString());
        assertThat(mvcResult.getResponse().getContentAsString()).contains(clubEntity.getIsActive().toString());
    }

    @Test
    public void shouldNotGetOneClub_InvalidId() throws Exception {
        Long clubId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/clube/{id}", clubId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
    }
}
package com.example.partidasdefutebol.tests.matches;

import com.example.partidasdefutebol.controller.MatchController;
import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.service.MatchService;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class matchesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldRetreiveMatchInfoByIdSucessfully() throws Exception {
        Long matchId = 6L;
        MvcResult mvcResult = mockMvc.perform(get("/partida/{id}", matchId))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

    }

    @Test
    public void throwExceptionAndWontGetMatch_InvalidMatchId() throws Exception {
        Long matchId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/partida/{id}", matchId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("A partida não existe na base de dados.");
    }

    @Test
    @Transactional
    public void souldDeleteMatchSuccessfully() throws Exception {
        Long matchId = 6L;
        MvcResult mvcResult = mockMvc.perform(delete("/partida/{id}", matchId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }

    @Test
    @Transactional
    public void shouldCreateMatchSuccessfully() throws Exception {
        MatchEntity match = new MatchEntity();
        match.setStadiumId(1L);
        match.setAwayClubId(2L);
        match.setHomeClubId(1L);
        match.setMatchDate(LocalDateTime.now());
        match.setHomeClubNumberOfGoals(1L);
        match.setAwayClubNumberOfGoals(0L);

        MvcResult mvcResult = mockMvc.perform(post("/partida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(match)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
    }


}

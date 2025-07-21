package com.example.partidasdefutebol.tests.matches;

import com.example.partidasdefutebol.controller.MatchController;
import com.example.partidasdefutebol.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
        Long matchId = 2L;
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
    public void souldDeleteMatchSuccessfully() throws Exception {
        Long matchId = 2L;
        MvcResult mvcResult = mockMvc.perform(get("/partida/{id}", matchId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }
}

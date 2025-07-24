package com.example.partidasdefutebol.tests.stadium;

import com.example.partidasdefutebol.controller.StadiumController;
import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.entities.StadiumFromController;
import com.example.partidasdefutebol.repository.StadiumRepository;
import com.example.partidasdefutebol.service.StadiumService;
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
    @Transactional
    public void shouldCreateStadiumSucessfully() throws Exception {
        StadiumFromController stadiumFromController = new StadiumFromController();
        stadiumFromController.setStadiumName("Nacional");
        stadiumFromController.setCep("01311000");

        MvcResult mvcResult = mockMvc.perform(post("/estadio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumFromController)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("SP");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Nacional");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("01311-000");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("São Paulo");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Avenida Paulista");

    }

    @Test
    @Transactional
    public void shouldThrowExceptionAndWontCreate_InvalidStadiumStateAcronym() throws Exception {
        StadiumFromController stadiumFromController = new StadiumFromController();
        stadiumFromController.setStadiumName("Nacional");
        stadiumFromController.setCep("00000000");
        MvcResult mvcResult = mockMvc.perform(post("/estadio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumFromController)))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(409);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O CEP informado não é válido.");
    }

    @Test
    @Transactional
    public void shoulduUpdateStadiumSucessfully() throws Exception {
        Long stadiumId = 2L;
        StadiumFromController stadiumFromController = new StadiumFromController();
        stadiumFromController.setStadiumName("Pacaembu");
        stadiumFromController.setCep("17860000");
        MvcResult mvcResult = mockMvc.perform(put("/estadio/{id}", stadiumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumFromController)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Pacaembu");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("SP");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("17860-000");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Pacaembu");

    }

    @Test
    public void shouldThrowExceptionAndDontUpdate_InvalidStadiumId() throws Exception {
        Long stadiumId = 100L;
        StadiumFromController stadiumFromController = new StadiumFromController();
        stadiumFromController.setStadiumName("Pacaembu");
        stadiumFromController.setCep("21941600");
        MvcResult mvcResult = mockMvc.perform(put("/estadio/{id}", stadiumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumFromController)))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O estádio não foi encontrado na base de dados.");
    }

    @Test
    public void shouldGetStadiumInfoSuccessfully() throws Exception {
        Long stadiumId = 6L;
        MvcResult mvcResult = mockMvc.perform(get("/estadio/{id}", stadiumId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Estádio Eucílio Viana");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("MA");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("65135-000");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("São José de Ribamar");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Rua Marinha Verde, s/n");
    }

    @Test
    public void shouldThrowExceptionAndWontGetStadium_InvalidStadiumId() throws Exception {
        Long stadiumId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/estadio/{id}", stadiumId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O estádio não foi encontrado na base de dados.");
    }

    @Test
    public void shouldRetrieveFilteredStadiums() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/estadio?state=SP"))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Estádio Municipal Jorge Ismael de Biasi");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Estádio Municipal Zico");
        assertThat(mvcResult.getResponse().getContentAsString()).doesNotContain("Estádio Dário Gomes");
    }

}

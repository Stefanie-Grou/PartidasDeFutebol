package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.dto.ControllerStadiumDTO;
import com.example.partidasdefutebol.repository.StadiumRepository;
import com.example.partidasdefutebol.service.ClubService;
import com.example.partidasdefutebol.service.StadiumService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
    @Autowired
    private ClubService clubService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCreateStadiumSucessfully() throws Exception {
        ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO("Nacional", "01311000");

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
    public void shouldThrowExceptionAndWontCreate_InvalidCEP() throws Exception {
        ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO();
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
    public void shoulduUpdateStadiumSucessfully() throws Exception {
        ControllerStadiumDTO stadiumToBeUpdated = new ControllerStadiumDTO("Nacional", "01311000");

        MvcResult postMvcResult = mockMvc.perform(post("/estadio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumToBeUpdated)))
                .andReturn();
        Long stadiumId = 1L;

        assertThat(postMvcResult.getResponse().getContentAsString()).contains("SP");
        assertThat(postMvcResult.getResponse().getContentAsString()).contains("Nacional");
        assertThat(postMvcResult.getResponse().getContentAsString()).contains("01311-000");
        assertThat(postMvcResult.getResponse().getContentAsString()).contains("São Paulo");
        assertThat(postMvcResult.getResponse().getContentAsString()).contains("Avenida Paulista");


        ControllerStadiumDTO stadiumNewData = new ControllerStadiumDTO("Pacaembu", "01311000");
        MvcResult putMvcResult = mockMvc.perform(put("/estadio/{id}", stadiumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumNewData)))
                .andReturn();

        assertThat(putMvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(putMvcResult.getResponse().getContentAsString()).contains("SP");
        assertThat(putMvcResult.getResponse().getContentAsString()).contains("Pacaembu");
        assertThat(putMvcResult.getResponse().getContentAsString()).contains("01311-000");
        assertThat(putMvcResult.getResponse().getContentAsString()).contains("São Paulo");
        assertThat(putMvcResult.getResponse().getContentAsString()).contains("Avenida Paulista");
    }

    @Test
    public void shouldThrowExceptionAndDontUpdate_InvalidStadiumId() throws Exception {
        Long stadiumId = 100L;
        ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO();
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
        ControllerStadiumDTO stadiumFromController = new ControllerStadiumDTO("Nacional", "01311000");

        mockMvc.perform(post("/estadio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stadiumFromController)))
                .andReturn();

        MvcResult mvcResult = mockMvc.perform(get("/estadio/{id}", 1L))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("SP");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Nacional");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("01311-000");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("São Paulo");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Avenida Paulista");
    }

    @Test
    public void shouldThrowExceptionAndWontGetStadium_InvalidStadiumId() throws Exception {
        Long stadiumId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/estadio/{id}", stadiumId))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("O estádio não foi encontrado na base de dados.");
    }

    /* problemas de sintaxe do h2
    @Test
    public void shouldRetrieveFilteredStadiums() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/estadio?state=SP"))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Estádio Municipal Jorge Ismael de Biasi");
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Estádio Municipal Zico");
        assertThat(mvcResult.getResponse().getContentAsString()).doesNotContain("Estádio Dário Gomes");
    }

     */
}

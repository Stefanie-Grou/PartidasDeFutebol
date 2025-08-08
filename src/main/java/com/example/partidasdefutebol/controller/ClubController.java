package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.dto.QueueMessageDTO;
import com.example.partidasdefutebol.entities.Club;
import com.example.partidasdefutebol.rabbitMQ.MessageSender;
import com.example.partidasdefutebol.service.ClubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import static com.example.partidasdefutebol.util.CheckValidBrazilianState.isValidBrazilianState;

@RestController
@RequestMapping("/clube")
@Tag(name = "Clube", description = "Gerenciamento dos registros de clubes, bem como buscas básicas edetalhadas")
public class ClubController {
    @Autowired
    private ClubService clubService;

    private final MessageSender messageSender;

    public ClubController(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Operation(summary = "Cria um clube no banco de dados, utilizando a fila de mensagens",
            description = "Recebe um clube no corpo da requisição e envia para a fila de mensagens, com algumas" +
                    " validações prévias do input. \n Não adiciona a mensagem à fila novamente em caso de erro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Aguardando processamento",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class,
                                    description = "Aguardando processamento"),
                            examples = @ExampleObject(value = "Aguardando processamento"))),
            @ApiResponse(responseCode = "404", description = "O estado XX é inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "{\"message\": \"O estado XX é inválido.\"}")))
    })
    @PostMapping
    public ResponseEntity<?> createClub(@Valid @RequestBody Club clubEntity) throws Exception {
        isValidBrazilianState(clubEntity.getStateAcronym());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        QueueMessageDTO queueMessage = new QueueMessageDTO("CREATE", clubEntity, null);
        messageSender.sendMessageToQueue(objectMapper.writeValueAsString(queueMessage));
        System.out.println("Message sent: " + clubEntity.getName());
        return ResponseEntity.status(202).body("Aguardando processamento");
    }

    @Operation(summary = "Atualiza um clube no banco de dados, utilizando a fila de mensagens",
            description = "Recebe um clube no corpo da requisição e envia para a fila de mensagens, com algumas" +
                    " validações prévias do input. \n Não adiciona a mensagem à fila novamente em caso de erro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Aguardando processamento",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class,
                                    description = "Aguardando processamento"),
                            examples = @ExampleObject(value = "Aguardando processamento"))),
            @ApiResponse(responseCode = "404", description = "Clube {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Clube {id} não encontrado na base de dados.")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClubById
            (@PathVariable Long id, @Valid @RequestBody Club requestedToUpdateClubEntity)
            throws JsonProcessingException {
        clubService.doesClubExist(id);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        QueueMessageDTO queueMessage = new QueueMessageDTO("UPDATE", requestedToUpdateClubEntity, id);
        messageSender.sendMessageToQueue(objectMapper.writeValueAsString(queueMessage));
        System.out.println("Message sent: " + requestedToUpdateClubEntity.getName());
        return ResponseEntity.status(202).body("Aguardando processamento");
    }

    @Operation(summary = "Deleta (soft delete) um clube do banco de dados",
            description = "Recebe um ID no path e deleta o clube correspondente, se ele existir.\n" +
                    "Não utiliza a fila de mensagens, na atual versão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Clube {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Clube {id} não encontrado na base de dados.")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClubById(@PathVariable Long id) {
        clubService.doesClubExist(id);
        Club onDeletionClub = clubService.deleteClub(id);
        return ResponseEntity.status(204).body(onDeletionClub);
    }

    @Operation(summary = "Busca os dados de um clube, baseado em seu ID",
            description = "Recebe um ID no path e retorna as informações do clube correspondente, se ele existir.\n" +
                    "Não utiliza a fila de mensagens, na atual versão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "name": "Operário",
                              "stateAcronym": "SP",
                              "createdOn": "2023-10-01",
                              "isActive": false
                            }"""))),
            @ApiResponse(responseCode = "404", description = "Clube {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Clube {id} não encontrado na base de dados.")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> fetchClubInfoById(@PathVariable Long id) {
        clubService.doesClubExist(id);
        return ResponseEntity.status(200).body(clubService.findClubById(id));
    }

    @Operation(summary = "Busca informações de todos os clubes",
            description = "Recebe filtros de busca e retorna os dados de todos os clubes, paginados por" +
                    " critério de filtro")
    @GetMapping
    public ResponseEntity<Page<Club>> getClubsInfoByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "clubName") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<Club> clubs = clubService.getClubs(name, state, isActive, page, size, sortField, sortOrder);
        return ResponseEntity.status(200).body(clubs);
    }

    @Operation(summary = "Busca o retrospecto de um clube por um dado ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "gols_feitos": 2,
                                "gols_sofridos": 0,
                                "total_de_vitorias": 2,
                                "total_de_empates": 0,
                                "total_de_derrotas": 0
                            }"""))),
            @ApiResponse(responseCode = "404", description = "Clube {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Clube {id} não encontrado na base de dados.")))
    })
    @GetMapping("/retrospecto/{id}")
    public ResponseEntity<?> getClubRetrospectiveById(@PathVariable Long id) {
        clubService.doesClubExist(id);
        return ResponseEntity.status(200).body(clubService.getClubRetrospective(id));
    }

    @Operation(summary = "Busca o retrospecto de um clube por oponente",
            description = "Retorna o retrospecto de um clube (por ID) por oponente registrado em partidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            [
                                    {
                                        "nome_do_oponente": "Novorizontino",
                                        "total_de_jogos": 2,
                                        "total_de_vitorias": 2,
                                        "total_de_empates": 0,
                                        "total_de_derrotas": 0,
                                        "total_de_gols_feitos": 2,
                                        "total_de_gols_sofridos": 0
                                    }
                                ]"""))),
            @ApiResponse(responseCode = "404", description = "Clube {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Clube {id} não encontrado na base de dados.")))
    })
    @GetMapping("/retrospecto-por-oponente/{id}")
    public ResponseEntity<?> getClubRetrospectiveByOpponent(@PathVariable Long id) {
        clubService.doesClubExist(id);
        return ResponseEntity.status(200).body(clubService.getClubRetrospectiveByOpponent(id));
    }

    @Operation(summary = "Ranqueia todos os clubes do banco de dados",
            description = "Ranqueia os clubes do banco de dados segundo: partidas, vitórias, gols ou pontos")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    [
                        {
                            "clube": "Vila Nova",
                            "total": 3
                        },
                        {
                            "clube": "Tigre",
                            "total": 3
                        }
                    ]""")))
    @GetMapping("/ranking")
    public ResponseEntity<?> getClubsRanking(
            @RequestParam String rankingFactor) {
        return ResponseEntity.status(200).body(clubService.callClubRankingDispatcher(rankingFactor));
    }
}

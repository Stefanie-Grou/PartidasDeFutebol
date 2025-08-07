package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.Matches;
import com.example.partidasdefutebol.service.MatchService;
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

@RestController
@RequestMapping("/partida")
@Tag(name = "Partidas", description = "Gerenciamento dos registros de partidas")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Operation(summary = "Cria uma partida", description = "Cria uma partida no banco de dados a " +
            " partir do JSON passado no corpo da request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "id": 7,
                                "homeClubId": 3,
                                "awayClubId": 5,
                                "homeClubNumberOfGoals": 1,
                                "awayClubNumberOfGoals": 1,
                                "stadiumId": 1,
                                "matchDate": "2025-10-10T15:30:00"
                            }"""))),
            @ApiResponse(responseCode = "400", description = "O clube está inativo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "O clube Botafogo-SP está inativo"
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "O time está em período de descanso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "O descanso mínimo para o clube é de 48 horas"
                                    }"""))),

    })
    @PostMapping
    public ResponseEntity<?> createMatch(@Valid @RequestBody Matches matchEntity) {
        return ResponseEntity.status(201).body(matchService.createMatch(matchEntity));
    }

    @Operation(summary = "Atualiza uma partida", description = "Recebe um ID no path e atualiza a " +
            " partida correspondente, se ela existir, com os dados passados no corpo da request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "id": 7,
                                "homeClubId": 3,
                                "awayClubId": 5,
                                "homeClubNumberOfGoals": 1,
                                "awayClubNumberOfGoals": 1,
                                "stadiumId": 1,
                                "matchDate": "2025-10-10T15:30:00"
                            }"""))),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "A partida não existe na base de dados."
                                    }"""))),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "O clube Botafogo-SP está inativo"
                                    }""")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatchById(@PathVariable Long id, @RequestBody Matches requestedToUpdateMatchEntity) {
        matchService.getMatchById(id);
        return ResponseEntity.status(200).body(matchService.updateMatch(id, requestedToUpdateMatchEntity));
    }

    @Operation(summary = "Deleta (hard delete) uma partida do banco de dados",
            description = "Recebe um ID no path e deleta a partida correspondente, se ela existir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Partida {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "A partida não existe na base de dados."
                                    }""")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatchById(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retorna os dados de uma partida do banco de dados, a partir de um ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "id": 7,
                                "homeClubId": 3,
                                "awayClubId": 5,
                                "homeClubNumberOfGoals": 1,
                                "awayClubNumberOfGoals": 1,
                                "stadiumId": 1,
                                "matchDate": "2025-10-10T15:30:00"
                            }"""))),
            @ApiResponse(responseCode = "404", description = "Partida {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Partida {id} não encontrado na base de dados.")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        return ResponseEntity.status(200).body(matchService.getMatchById(id));
    }

    @Operation(summary = "Retorna as partidas do banco de dados, a partir de filtros")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                     {
                        "id": 7,
                        "homeClubId": 3,
                        "awayClubId": 5,
                        "homeClubNumberOfGoals": 1,
                        "awayClubNumberOfGoals": 1,
                        "stadiumId": 1,
                        "matchDate": "2025-10-10T15:30:00"
                    }""")))
    @GetMapping
    public ResponseEntity<Page<Matches>> getMatchesByFilters(
            @RequestParam(required = false) Long club,
            @RequestParam(required = false) Long stadium,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "homeClubId") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "false") Boolean isRout,
            @RequestParam(required = false) String showOnly) {
        Page<Matches> matches = matchService.getMatches
                (club, stadium, page, size, sortField, sortOrder, isRout, showOnly);
        return ResponseEntity.status(200).body(matches);
    }

    @Operation(summary = "Retorna os dados de partidas entre dois clubes",
            description = "Recebe dois IDs no path e retorna os dados de partidas entre os dois clubes relacionados" +
                    ", caso existam.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    [
                        {
                            "clube_casa": "Botafogo-SP",
                            "clube_visitante": "Novorizontino",
                            "gols_do_clube_casa": 1,
                            "gols_do_clube_visitante": 0,
                            "vencedor": "Botafogo-SP"
                        },
                        {
                            "clube_casa": "Novorizontino",
                            "clube_visitante": "Botafogo-SP",
                            "gols_do_clube_casa": 0,
                            "gols_do_clube_visitante": 1,
                            "vencedor": "Botafogo-SP"
                        }
                    ]""")))
    @GetMapping("/{id1}/versus/{id2}")
    public ResponseEntity<?> getMatchesBetweenTwoClubs(@PathVariable Long id1, @PathVariable Long id2) {
        return ResponseEntity.status(200).body(matchService.getMatchBetweenClubs(id1, id2));
    }

    @Operation(summary = "Retorna as goleadas entre times",
            description = "Retorna os dados de todas as partidas com uma diferença igual ou superior" +
            " a três gols entre os times")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "partida": "Vila Nova 3 X Paduano RJ 0",
                        "estadio": "Estádio Joaquim de Almeida Freitas",
                        "ocorreu_em": "01-12-2019"
                    }""")))
    @GetMapping("/goleadas")
    public ResponseEntity<?> getAllRouts() {
        return ResponseEntity.status(200).body(matchService.getAllRouts());
    }
}
package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.Stadium;
import com.example.partidasdefutebol.dto.ControllerStadiumDTO;
import com.example.partidasdefutebol.service.StadiumService;
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

@Tag(name = "Estadio", description = "Gerenciamento de registros de estádios")
@RestController
@RequestMapping("/estadio")
public class StadiumController {
    @Autowired
    private StadiumService stadiumService;

    @Operation(summary = "Registra um estádio no banco de dados",
            description = "Recebe um JSON com os dados do estádio e o salva no banco de dados." +
                    " Utiliza a API ViaCEP para buscar o endereço completo do estádio, retornando um DTO" +
                    " das informaçòes completas do registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "id": 11,
                                "name": "Pacaembu",
                                "stateAcronym": "SP",
                                "city": "São Paulo",
                                "street": "Rua Atílio Vivácqua",
                                "cep": "05397-220"
                            }"""))),
            @ApiResponse(responseCode = "400", description = "Entrada para CEP e nome são inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "stadiumName": "O nome do estadio deve ser composto por, no mínimo, 3 letras.",
                                        "cep": "O CEP do estádio deve ser composto por 8 números, sem traço."
                                    }""")))

    })
    @PostMapping
    public ResponseEntity<?> saveStadium(@Valid @RequestBody ControllerStadiumDTO stadiumFromController) {
        return ResponseEntity.status(201).body(stadiumService.saveStadium(stadiumFromController));
    }

    @Operation(summary = "Atualiza um estádio no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "id": 11,
                                "name": "Pacaembu",
                                "stateAcronym": "SP",
                                "city": "São Paulo",
                                "street": "Rua Atílio Vivácqua",
                                "cep": "05397-220"
                            }"""))),
            @ApiResponse(responseCode = "400", description = "Entrada para CEP e nome são inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "O estádio não foi encontrado na base de dados."
                                    }""")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStadium(
            @PathVariable Long id,
            @Valid @RequestBody ControllerStadiumDTO stadiumFromController
    ) {
        return ResponseEntity.status(200).body(stadiumService.updateStadium(id, stadiumFromController));
    }

    @Operation(summary = "Retorna os dados de um estádio do banco de dados, a partir de um ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                                                    {
                            "id": 8,
                            "name": "Estádio Municipal Genésio de Almeida",
                            "stateAcronym": "AL",
                            "city": "Pilar",
                            "street": "Avenida Nossa Senhora da Esperança, 196",
                            "cep": "57160-000"
                                                    }"""))),
            @ApiResponse(responseCode = "404", description = "Estadio {id} não encontrado na base de dados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class,
                                    description = "Modelo de erro"),
                            examples = @ExampleObject(value = "Estadio {id} não encontrado na base de dados.")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findStadiumById(@PathVariable Long id) {
        stadiumService.doesStadiumExist(id);
        ResponseEntity<Stadium> optionalStadium = stadiumService.retrieveStadiumInfo(id);
        return ResponseEntity.status(optionalStadium.getStatusCode()).body(optionalStadium.getBody());
    }

    @Operation(summary = "Retorna os dados de estadios do banco de dados, a partir de filtros. ",
            description = "O ordenamento padrão é realizado ascendentemente pela coluna 'name', " +
                    "podendo ser alterado com o parâmetro 'sortStadiumsByField' e 'sortOrder'. " +
                    "Os parâmetros 'name' e 'state' podem ser usados para filtrar os registros.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                            {
                            "id": 8,
                            "name": "Estádio Municipal Genésio de Almeida",
                            "stateAcronym": "AL",
                            "city": "Pilar",
                            "street": "Avenida Nossa Senhora da Esperança, 196",
                            "cep": "57160-000"
                            }""")))
    @GetMapping
    public ResponseEntity<Page<Stadium>> getStadiumsByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortStadiumsByField,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Stadium> stadiums = stadiumService.getStadiums
                (name, state, page, size, sortStadiumsByField, sortOrder);
        return ResponseEntity.status(200).body(stadiums);
    }
}
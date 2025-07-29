package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partida")
public class MatchController {

    @Autowired
    private MatchService matchService;

    // Requisito I#6 -> Criar uma partida
    @PostMapping
    public ResponseEntity<?> createMatch(@Valid @RequestBody MatchEntity matchEntity) {
        try {
            MatchEntity savedMatchEntity = matchService.createMatch(matchEntity);
            return ResponseEntity.status(201).body(savedMatchEntity);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    // Requisito I#7 -> Atualizar uma partida
    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable Long id, @RequestBody MatchEntity requestedToUpdateMatchEntity) {
        try {
            MatchEntity updatedMatchEntity = matchService.updateMatch(id, requestedToUpdateMatchEntity);
            return ResponseEntity.status(200).body(updatedMatchEntity);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    // Requisito I#8 -> Deletar uma partida
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        try {
            matchService.deleteMatch(id);
            return ResponseEntity.noContent().build();
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    // Requisito I#9 -> Buscar uma partida
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        try {
            matchService.getMatchById(id);
            return ResponseEntity.status(200).body(matchService.getMatchById(id));
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    // Requisito I#10 -> Buscar todas as partidas + III#1 -> Adicionar goleadas
    @GetMapping
    public ResponseEntity<Page<MatchEntity>> getMatches(
            @RequestParam(required = false) Long club,
            @RequestParam(required = false) Long stadium,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "homeClubId") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "false") Boolean isRout,
            @RequestParam(required = false) String showOnly) {
        Page<MatchEntity> matches = matchService.getMatches
                (club, stadium, page, size, sortField, sortOrder, isRout, showOnly);
        return ResponseEntity.ok(matches);
    }

    // Requisito II#3 -> Buscar uma partida entre dois clubes
    @GetMapping("/{id1}/versus/{id2}")
    public ResponseEntity<?> getMatchBetweenClubs(@PathVariable Long id1, @PathVariable Long id2) {
        try {
            return ResponseEntity.status(200).body(matchService.getMatchBetweenClubs(id1, id2));
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/goleadas")
    public ResponseEntity<?> getAllRouts() {
        return ResponseEntity.status(200).body(matchService.getAllRouts());
    }
}
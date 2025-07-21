package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/partida")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchEntity> createMatch(@Valid @RequestBody MatchEntity matchEntity) {
        try {
            MatchEntity savedMatchEntity = matchService.createMatch(matchEntity);
            return ResponseEntity.status(201).body(savedMatchEntity);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<MatchEntity> updateMatch(@PathVariable Long id, @RequestBody MatchEntity requestedToUpdateMatchEntity) {
        try {
            MatchEntity updatedMatchEntity = matchService.updateMatch(id, requestedToUpdateMatchEntity);
            return ResponseEntity.status(200).body(updatedMatchEntity);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        try {
            matchService.deleteMatch(id);
            return ResponseEntity.noContent().build();
        } catch (ConflictException e) {
            throw new ConflictException(e.getMessage(), e.getStatusCode());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        try {
            matchService.getMatchById(id);
            return ResponseEntity.status(200).body(matchService.getMatchById(id));
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<MatchEntity>> getMatches(
            @RequestParam(required = false) Long club,
            @RequestParam(required = false) Long stadium,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "homeClubId") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<MatchEntity> matches = matchService.getMatches(club, stadium, page, size, sortField, sortOrder);
        return ResponseEntity.ok(matches);
    }
}
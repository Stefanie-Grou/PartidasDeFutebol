package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.GoalSummary;
import com.example.partidasdefutebol.entities.SummaryByOpponent;
import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.service.ClubService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clube")
public class ClubController {
    @Autowired
    private ClubService clubService;

    @PostMapping
    public ResponseEntity<?> createClub(@Valid @RequestBody ClubEntity clubEntity) {
        try {
            ClubEntity savedClubEntity = clubService.createClub(clubEntity);
            return ResponseEntity.status(201).body(savedClubEntity);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClub(@PathVariable Long id, @Valid @RequestBody ClubEntity requestedToUpdateClubEntity) {
        try {
            ClubEntity savedClubEntity = clubService.updateClub(id, requestedToUpdateClubEntity);
            return ResponseEntity.status(200).body(savedClubEntity);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClub(@PathVariable Long id) {
        try {
            ClubEntity onDeletionClub = clubService.deleteClub(id);
            return ResponseEntity.status(204).body(onDeletionClub);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(200).body(clubService.findClubById(id));
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<ClubEntity>> getClubs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "clubName") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<ClubEntity> clubs = clubService.getClubs(name, state, isActive, page, size, sortField, sortOrder);
        return ResponseEntity.ok(clubs);
    }

    @GetMapping("/retrospecto/{id}")
    public ResponseEntity<?> getClubRetrospective(@PathVariable Long id) {
        try {
            GoalSummary goalSummary = clubService.getClubRetrospective(id);
            return ResponseEntity.status(200).body(goalSummary);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/retrospecto-por-oponente/{id}")
    public ResponseEntity<?> getClubRetrospectiveByOpponent(@PathVariable Long id) {
        try {
            Page<SummaryByOpponent> summaryByOpponent = clubService.getClubRetrospectiveByOpponent(id);
            return ResponseEntity.status(200).body(summaryByOpponent);
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getClubRanking(
            @RequestParam String rankingFactor) {
        try {
            return ResponseEntity.status(200).body(clubService.getClubRankingDispatcher(rankingFactor));
        } catch (ConflictException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }
}

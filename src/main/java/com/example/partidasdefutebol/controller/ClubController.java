package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.repository.ClubRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/clube")
public class ClubController {
    @Autowired
    private ClubRepository clubRepository;

    @PostMapping
    public ResponseEntity<ClubEntity> save(@Valid @RequestBody ClubEntity clubEntity) {
        try {
            ClubEntity savedClubEntity = clubRepository.save(clubEntity);
            return ResponseEntity.status(201).body(savedClubEntity);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClubEntity requestedToUpdateClubEntity) {
        Optional<ClubEntity> clubOptional = clubRepository.findById(id);
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        ClubEntity existingClubEntity = clubOptional.get();
        existingClubEntity.setClubName(requestedToUpdateClubEntity.getClubName());
        existingClubEntity.setStateAcronym(requestedToUpdateClubEntity.getStateAcronym());
        existingClubEntity.setCreatedOn(requestedToUpdateClubEntity.getCreatedOn());
        existingClubEntity.setIsActive(requestedToUpdateClubEntity.getIsActive());
        try {
            ClubEntity updatedClubEntity = clubRepository.save(existingClubEntity);
            return ResponseEntity.status(200).body(updatedClubEntity);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<ClubEntity> clubOptional = clubRepository.findById(id);
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        ClubEntity existingClubEntity = clubOptional.get();
        clubOptional.get().setIsActive(false);
        clubRepository.save(existingClubEntity);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubEntity> findById(@PathVariable Long id) {
        Optional<ClubEntity> clubOptional = clubRepository.findById(id);
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        ClubEntity existingClubEntity = clubOptional.get();
        return ResponseEntity.status(200).body(existingClubEntity);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<ClubEntity>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "clubName") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String orderBy) {

        Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sorting = Sort.by(direction, sort);
        Page<ClubEntity> paginatedClubs = clubRepository.findAll(PageRequest.of(page, size, sorting));
        if (paginatedClubs.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(paginatedClubs);
    }
}

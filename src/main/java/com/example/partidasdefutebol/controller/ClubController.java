package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.repository.Club;
import com.example.partidasdefutebol.repository.ClubRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/clube")
public class ClubController {
    @Autowired
    private ClubRepository clubRepository;

    @PostMapping
    public ResponseEntity<Club> save(@Valid @RequestBody Club club) {
        if (clubRepository.existsByClubNameAndStateAcronym(club.getClubName(), club.getStateAcronym())) {
            return ResponseEntity.status(409).build();
        }

        try {
            Club savedClub = clubRepository.save(club);
            return ResponseEntity.status(201).body(savedClub);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Club requestedToUpdateClub) {
        Optional<Club> clubOptional = clubRepository.findById(id);
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        Club existingClub = clubOptional.get();
        existingClub.setClubName(requestedToUpdateClub.getClubName());
        existingClub.setStateAcronym(requestedToUpdateClub.getStateAcronym());
        existingClub.setCreatedOn(requestedToUpdateClub.getCreatedOn());
        existingClub.setIsActive(requestedToUpdateClub.getIsActive());
        try {
            Club updatedClub = clubRepository.save(existingClub);
            return ResponseEntity.status(200).body(updatedClub);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Club> clubOptional = clubRepository.findById(id);
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        Club existingClub = clubOptional.get();
        clubOptional.get().setIsActive(false);
        clubRepository.save(existingClub);
        return ResponseEntity.status(204).build();
    }
}

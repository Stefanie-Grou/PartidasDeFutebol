package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.repository.StadiumRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/estadio")
public class StadiumController {
    @Autowired
    private StadiumRepository stadiumRepository;

    @PostMapping
    public ResponseEntity<StadiumEntity> save(@Valid @RequestBody StadiumEntity stadiumEntity) {
        try {
            StadiumEntity savedStadiumEntity = stadiumRepository.save(stadiumEntity);
            return ResponseEntity.status(201).body(savedStadiumEntity);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<StadiumEntity> update(@PathVariable Long id, @Valid @RequestBody StadiumEntity requestedToUpdateStadiumEntity) {

        Optional<StadiumEntity> optionalStadium = stadiumRepository.findById(id);

        if (optionalStadium.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        StadiumEntity existingStadiumEntity = optionalStadium.get();
        existingStadiumEntity.setStadiumName(requestedToUpdateStadiumEntity.getStadiumName());
        existingStadiumEntity.setStadiumState(requestedToUpdateStadiumEntity.getStadiumState());
        try {
            StadiumEntity updatedStadiumEntity = stadiumRepository.save(existingStadiumEntity);
            return ResponseEntity.status(200).body(updatedStadiumEntity);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StadiumEntity> findById(@PathVariable Long id) {
        Optional<StadiumEntity> optionalStadium = stadiumRepository.findById(id);
        return optionalStadium.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}


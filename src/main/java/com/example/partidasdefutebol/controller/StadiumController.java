package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.repository.Stadium;
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
    public ResponseEntity<Stadium> save(@Valid @RequestBody Stadium stadium) {
        try {
            Stadium savedStadium = stadiumRepository.save(stadium);
            return ResponseEntity.status(201).body(savedStadium);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<Stadium> update(@PathVariable Long id, @Valid @RequestBody Stadium requestedToUpdateStadium) {

        Optional<Stadium> optionalStadium = stadiumRepository.findById(id);

        if (optionalStadium.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Stadium existingStadium = optionalStadium.get();
        existingStadium.setStadiumName(requestedToUpdateStadium.getStadiumName());
        existingStadium.setStadiumState(requestedToUpdateStadium.getStadiumState());
        try {
            Stadium updatedStadium = stadiumRepository.save(existingStadium);
            return ResponseEntity.status(200).body(updatedStadium);
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stadium> findById(@PathVariable Long id) {
        Optional<Stadium> optionalStadium = stadiumRepository.findById(id);
        if (optionalStadium.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(optionalStadium.get());
        }
    }
}

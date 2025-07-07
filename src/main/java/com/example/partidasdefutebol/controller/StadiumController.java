package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.repository.Club;
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
}

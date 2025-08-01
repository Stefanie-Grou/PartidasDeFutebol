package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.entities.StadiumFromController;
import com.example.partidasdefutebol.exceptions.CustomException;
import com.example.partidasdefutebol.service.StadiumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estadio")
public class StadiumController {
    @Autowired
    private StadiumService stadiumService;

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody StadiumFromController stadiumFromController) {
        try {
            StadiumEntity savedStadiumEntity = stadiumService.saveStadium(stadiumFromController);
            return ResponseEntity.status(201).body(savedStadiumEntity);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody StadiumFromController stadiumFromController
    ) {
        try {
            StadiumEntity updatedStadiumEntity = stadiumService.updateStadium(id, stadiumFromController);
            return ResponseEntity.status(200).body(updatedStadiumEntity);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            ResponseEntity<StadiumEntity> optionalStadium = stadiumService.retrieveStadiumInfo(id);
            return ResponseEntity.status(optionalStadium.getStatusCode()).body(optionalStadium.getBody());
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<StadiumEntity>> getStadiums(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stadiumName") String sortStadiumsByField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<StadiumEntity> stadiums = stadiumService.getStadiums(name, state, page, size, sortStadiumsByField, sortOrder);
        return ResponseEntity.ok(stadiums);
    }
}
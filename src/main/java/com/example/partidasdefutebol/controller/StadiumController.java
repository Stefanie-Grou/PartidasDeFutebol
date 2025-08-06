package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.Stadium;
import com.example.partidasdefutebol.dto.ControllerStadiumDTO;
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
    public ResponseEntity<?> saveStadium(@Valid @RequestBody ControllerStadiumDTO stadiumFromController) {
        return ResponseEntity.status(201).body(stadiumService.saveStadium(stadiumFromController));
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> updateStadium(
            @PathVariable Long id,
            @Valid @RequestBody ControllerStadiumDTO stadiumFromController
    ) {
        return ResponseEntity.status(200).body(stadiumService.updateStadium(id, stadiumFromController));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findStadiumById(@PathVariable Long id) {
        stadiumService.doesStadiumExist(id);
        ResponseEntity<Stadium> optionalStadium = stadiumService.retrieveStadiumInfo(id);
        return ResponseEntity.status(optionalStadium.getStatusCode()).body(optionalStadium.getBody());
    }

    @GetMapping
    public ResponseEntity<Page<Stadium>> getStadiumsByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stadiumName") String sortStadiumsByField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<Stadium> stadiums = stadiumService.getStadiums(name, state, page, size, sortStadiumsByField, sortOrder);
        return ResponseEntity.status(200).body(stadiums);
    }
}
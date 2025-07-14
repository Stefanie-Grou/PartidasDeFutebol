package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping("/partida")
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
}

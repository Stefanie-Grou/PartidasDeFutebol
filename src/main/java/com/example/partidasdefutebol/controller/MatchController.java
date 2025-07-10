package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequestMapping
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @PostMapping("/partida")
    public ResponseEntity<MatchEntity> createMatch(@Valid @RequestBody MatchEntity matchEntity) {
        String showErrorMessageToUser;
        if (Objects.equals(matchEntity.getAwayClubId(), matchEntity.getHomeClubId())) {
            showErrorMessageToUser = "Os identificados dos times precisam ser diferentes entre si";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, showErrorMessageToUser);
        }
        try {
            MatchEntity savedMatchEntity = matchRepository.save(matchEntity);
            return ResponseEntity.status(201).body(savedMatchEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os dados inseridos são inválidos para a criação de uma partida");
        }
    }
}

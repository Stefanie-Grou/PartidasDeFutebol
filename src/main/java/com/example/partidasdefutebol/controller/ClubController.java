package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.repository.Club;
import com.example.partidasdefutebol.repository.ClubRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/criar-clube")
public class ClubController {
    @Autowired
    private ClubRepository clubRepository;

    @PostMapping
    public ResponseEntity<Club> save(@Valid @RequestBody Club club) {
        //int totalClubInDb = ClubService.totalClubsInDb(club.getClubName(), club.getStateAcronym());
        if (clubRepository.existsByClubNameAndStateAcronym(club.getClubName(), club.getStateAcronym())) {
            return ResponseEntity.status(409).build();
        }
        Club savedClub = clubRepository.save(club);
        return ResponseEntity.status(201).body(savedClub);
    }

}

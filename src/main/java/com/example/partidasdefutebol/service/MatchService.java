package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.repository.ClubRepository;
import com.example.partidasdefutebol.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ClubService clubService;

    public MatchEntity save(MatchEntity matchEntity) {
        return matchRepository.save(matchEntity);
    }


}
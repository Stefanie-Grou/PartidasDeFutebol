package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.repository.Club;
import com.example.partidasdefutebol.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubService {

    @Autowired
    private static ClubRepository clubRepository;
    public Club save(Club club) {
        return clubRepository.save(club);
    }
}

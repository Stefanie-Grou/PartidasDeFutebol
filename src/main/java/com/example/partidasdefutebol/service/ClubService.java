package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.repository.Club;
import com.example.partidasdefutebol.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    @Autowired
    private static ClubRepository clubRepository;

    public Club save(Club club) {
        return clubRepository.save(club);
    }

    public Club update(Long id, Club requestedToUpdateClub) {
        return clubRepository.save(requestedToUpdateClub);
    }
}

package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClubService {

    @Autowired
    private static ClubRepository clubRepository;

    public ClubEntity save(ClubEntity clubEntity) {
        return clubRepository.save(clubEntity);
    }

    public ClubEntity update(Long id, ClubEntity requestedToUpdateClubEntity) {
        return clubRepository.save(requestedToUpdateClubEntity);
    }
}

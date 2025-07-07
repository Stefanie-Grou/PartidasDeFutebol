package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.repository.Stadium;
import com.example.partidasdefutebol.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StadiumService {

    @Autowired
    private static StadiumRepository stadiumRepository;

    public Stadium save(Stadium stadium) {
        return stadiumRepository.save(stadium);
    }
}

package com.example.partidasdefutebol.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    boolean existsByClubNameAndStateAcronym(String clubName, String stateAcronym);

}

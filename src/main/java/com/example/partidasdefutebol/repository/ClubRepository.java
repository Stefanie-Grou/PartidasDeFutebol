package com.example.partidasdefutebol.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
    boolean existsByClubNameAndStateAcronym(String clubName, String stateAcronym);
}

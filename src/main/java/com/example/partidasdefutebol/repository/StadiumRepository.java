package com.example.partidasdefutebol.repository;

import com.example.partidasdefutebol.entities.StadiumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<StadiumEntity, Long> {
}

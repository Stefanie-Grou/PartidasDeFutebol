package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    public ClubEntity createClub(ClubEntity clubEntity) {
        return clubRepository.save(clubEntity);
    }

    public void doesClubExist(Long clubId) throws ResponseStatusException {
        if (!clubRepository.existsById(clubId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public ClubEntity updateClub(Long id, ClubEntity requestedToUpdateClubEntity) {
        doesClubExist(id);
        ClubEntity clubEntity = clubRepository.findById(id).get();
        clubEntity.setClubName(requestedToUpdateClubEntity.getClubName());
        clubEntity.setStateAcronym(requestedToUpdateClubEntity.getStateAcronym());
        clubEntity.setCreatedOn(requestedToUpdateClubEntity.getCreatedOn());
        clubEntity.setIsActive(requestedToUpdateClubEntity.getIsActive());
        return clubRepository.saveAndFlush(clubEntity);
    }

    public ClubEntity deleteClub(Long id) {
        doesClubExist(id);
        ClubEntity existingClubEntity = clubRepository.findById(id).get();
        existingClubEntity.setIsActive(false);
        return clubRepository.save(existingClubEntity);
    }

    public ClubEntity findClubById(Long id) {
        doesClubExist(id);
        return clubRepository.findById(id).get();
    }

    public Page<ClubEntity> getClubs(String name, String state, Boolean isActive, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return clubRepository.findByFilters(name, state, isActive, pageRequest);
    }

    public void wasClubCreatedBeforeGame(Long clubid,
                                           LocalDateTime matchDate) {
        if (clubRepository.findById(clubid).get().getCreatedOn().isAfter(ChronoLocalDate.from(matchDate))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public void isAnyOfClubsInactive (ClubEntity homeClubEntity, ClubEntity awayClubEntity) {
        if (!homeClubEntity.getIsActive() || !awayClubEntity.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}

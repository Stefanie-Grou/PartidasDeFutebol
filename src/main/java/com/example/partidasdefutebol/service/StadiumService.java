package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StadiumService {

    @Autowired
    private StadiumRepository stadiumRepository;

    public StadiumEntity saveStadium(StadiumEntity stadiumEntity) {
        return stadiumRepository.save(stadiumEntity);
    }

    public StadiumEntity updateStadium(
            Long stadiumId,
            StadiumEntity requestedToUpdateStadiumEntity) {
        stadiumRepository.findById(stadiumId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        StadiumEntity existingStadiumEntity = stadiumRepository.findById(stadiumId).get();
        existingStadiumEntity.setStadiumName(requestedToUpdateStadiumEntity.getStadiumName());
        existingStadiumEntity.setStadiumState(requestedToUpdateStadiumEntity.getStadiumState());
        return stadiumRepository.saveAndFlush(existingStadiumEntity);
    }

    public ResponseEntity<StadiumEntity> retrieveStadiumInfo(Long requestedStadiumId) {
        Optional<StadiumEntity> optionalStadium = stadiumRepository.findById(requestedStadiumId);
        if (optionalStadium.isPresent()) {
            StadiumEntity stadiumEntity = optionalStadium.get();
            return ResponseEntity.ok(stadiumEntity);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Page<StadiumEntity> getStadiums
            (String name, String state, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return stadiumRepository.findStadiumsByFilters(name, state, pageRequest);
    }

    public void doesStadiumExist(Long stadiumId) throws ResponseStatusException {
        if (!stadiumRepository.existsById(stadiumId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

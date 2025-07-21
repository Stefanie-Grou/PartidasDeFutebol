package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
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

import static com.example.partidasdefutebol.util.isValidBrazilianState.isValidBrazilianState;

@Service
public class StadiumService {

    @Autowired
    private StadiumRepository stadiumRepository;

    public StadiumEntity saveStadium(StadiumEntity stadiumEntity) {
        if (!isValidBrazilianState(stadiumEntity.getStadiumState())) {
            throw new ConflictException("A sigla do estado é inválida.", 409);
        }
        return stadiumRepository.save(stadiumEntity);
    }

    public StadiumEntity updateStadium(
            Long stadiumId,
            StadiumEntity requestedToUpdateStadiumEntity) {
        doesStadiumExist(stadiumId);
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
            throw new ConflictException("O estádio não foi encontrado na base de dados.", 404);
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
            throw new ConflictException("O estádio nao foi encontrado na base de dados.", 404);
        }
    }
}

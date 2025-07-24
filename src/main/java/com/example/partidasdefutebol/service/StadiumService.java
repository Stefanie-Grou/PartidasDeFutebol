package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.AddressEntity;
import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.entities.StadiumFromController;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class StadiumService {

    @Autowired
    private StadiumRepository stadiumRepository;

    public StadiumEntity saveStadium(StadiumFromController stadiumFromController) {
        AddressEntity addressInfo = AddressSearchService.findFullAddressByCep(stadiumFromController.getCep());
        StadiumEntity stadium = new StadiumEntity();
        stadium.setStadiumName(stadiumFromController.getStadiumName());
        stadium.setStadiumState(addressInfo.getState());
        stadium.setStreet(addressInfo.getStreet());
        stadium.setCity(addressInfo.getCity());
        stadium.setCep(addressInfo.getCep());
        return stadiumRepository.save(stadium);
    }

    public StadiumEntity updateStadium(
            Long stadiumId,
            StadiumFromController requestedToUpdateStadiumEntity) {
        doesStadiumExist(stadiumId);
        StadiumEntity existingStadiumEntity = stadiumRepository.findById(stadiumId).get();
        AddressEntity addressInfo = AddressSearchService.findFullAddressByCep(requestedToUpdateStadiumEntity.getCep());
        existingStadiumEntity.setStreet(addressInfo.getStreet());
        existingStadiumEntity.setCity(addressInfo.getCity());
        existingStadiumEntity.setCep(addressInfo.getCep());
        existingStadiumEntity.setStadiumState(addressInfo.getState());
        existingStadiumEntity.setStadiumName(requestedToUpdateStadiumEntity.getStadiumName());
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
            throw new ConflictException("O estádio não foi encontrado na base de dados.", 404);
        }
    }
}

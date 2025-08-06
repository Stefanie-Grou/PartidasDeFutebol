package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.dto.AddressDTO;
import com.example.partidasdefutebol.entities.Stadium;
import com.example.partidasdefutebol.dto.ControllerStadiumDTO;
import com.example.partidasdefutebol.exceptions.CustomException;
import com.example.partidasdefutebol.repository.StadiumRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
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

    public Stadium saveStadium(ControllerStadiumDTO stadiumFromController) {
        AddressDTO addressInfo = AddressSearchService.findFullAddressByCep(stadiumFromController.getCep());
        Stadium stadium = new Stadium();
        stadium.setName(stadiumFromController.getStadiumName());
        stadium.setStateAcronym(addressInfo.getState());
        stadium.setStreet(addressInfo.getStreet());
        stadium.setCity(addressInfo.getCity());
        stadium.setCep(addressInfo.getCep());
        return stadiumRepository.save(stadium);
    }

    public Stadium updateStadium(
            Long stadiumId,
            ControllerStadiumDTO requestedToUpdateStadiumEntity) {
        doesStadiumExist(stadiumId);
        Stadium existingStadiumEntity = stadiumRepository.findById(stadiumId).get();
        AddressDTO addressInfo = AddressSearchService.findFullAddressByCep(requestedToUpdateStadiumEntity.getCep());
        existingStadiumEntity.setStreet(addressInfo.getStreet());
        existingStadiumEntity.setCity(addressInfo.getCity());
        existingStadiumEntity.setCep(addressInfo.getCep());
        existingStadiumEntity.setStateAcronym(addressInfo.getState());
        existingStadiumEntity.setName(requestedToUpdateStadiumEntity.getStadiumName());
        return stadiumRepository.saveAndFlush(existingStadiumEntity);
    }

    public ResponseEntity<Stadium> retrieveStadiumInfo(Long requestedStadiumId) {
        doesStadiumExist(requestedStadiumId);
        return ResponseEntity.ok(stadiumRepository.findById(requestedStadiumId).get());
    }

    public Page<Stadium> getStadiums
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
            //throw new CustomException("O estádio não foi encontrado na base de dados.", 404);
            throw new AmqpRejectAndDontRequeueException("O estádio não foi encontrado na base de dados.");
        }
    }
}

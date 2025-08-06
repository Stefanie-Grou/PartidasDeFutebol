package com.example.partidasdefutebol.controller;

import com.example.partidasdefutebol.dto.QueueMessageDTO;
import com.example.partidasdefutebol.entities.Club;
import com.example.partidasdefutebol.rabbitMQ.MessageSender;
import com.example.partidasdefutebol.service.ClubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.partidasdefutebol.util.CheckValidBrazilianState.isValidBrazilianState;

@RestController
@RequestMapping("/clube")
public class ClubController {
    @Autowired
    private ClubService clubService;

    private final MessageSender messageSender;

    public ClubController(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @PostMapping
    public ResponseEntity<?> createClub(@Valid @RequestBody Club clubEntity) throws Exception {
        isValidBrazilianState(clubEntity.getStateAcronym());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        QueueMessageDTO queueMessage = new QueueMessageDTO("CREATE", clubEntity, null);
        messageSender.sendMessageToQueue(objectMapper.writeValueAsString(queueMessage));
        System.out.println("Message sent: " + clubEntity.getName());
        return ResponseEntity.status(202).body("Aguardando processamento");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClubById
            (@PathVariable Long id, @Valid @RequestBody Club requestedToUpdateClubEntity)
            throws JsonProcessingException {
        clubService.doesClubExist(id);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        QueueMessageDTO queueMessage = new QueueMessageDTO("UPDATE", requestedToUpdateClubEntity, id);
        messageSender.sendMessageToQueue(objectMapper.writeValueAsString(queueMessage));
        System.out.println("Message sent: " + requestedToUpdateClubEntity.getName());
        return ResponseEntity.status(202).body("Aguardando processamento");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClubById(@PathVariable Long id) throws Exception {
        clubService.doesClubExist(id);
        Club onDeletionClub = clubService.deleteClub(id);
        return ResponseEntity.status(204).body(onDeletionClub);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> fetchClubInfoById(@PathVariable Long id) {
        clubService.doesClubExist(id);
        return ResponseEntity.status(200).body(clubService.findClubById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Club>> getClubsInfoByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "clubName") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<Club> clubs = clubService.getClubs(name, state, isActive, page, size, sortField, sortOrder);
        return ResponseEntity.status(200).body(clubs);
    }

    @GetMapping("/retrospecto/{id}")
    public ResponseEntity<?> getClubRetrospectiveById(@PathVariable Long id) {
        clubService.doesClubExist(id);
        return ResponseEntity.status(200).body(clubService.getClubRetrospective(id));
    }

    @GetMapping("/retrospecto-por-oponente/{id}")
    public ResponseEntity<?> getClubRetrospectiveByOpponent(@PathVariable Long id) {
        clubService.doesClubExist(id);
        return ResponseEntity.status(200).body(clubService.getClubRetrospectiveByOpponent(id));
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getClubsRanking(
            @RequestParam String rankingFactor) {
        return ResponseEntity.status(200).body(clubService.callClubRankingDispatcher(rankingFactor));
    }
}

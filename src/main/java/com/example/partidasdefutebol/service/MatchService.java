package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ClubService clubService;

    @Autowired
    private StadiumService stadiumService;

    public MatchEntity createMatch(MatchEntity matchEntity) {
        CallCommonValidationForMatch(matchEntity);
        isEachClubDifferent(matchEntity);
        clubService.wasClubCreatedBeforeGame(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        clubService.wasClubCreatedBeforeGame(matchEntity.getAwayClubId(), matchEntity.getMatchDate());
        clubService.isClubInactive(clubService.findClubById(matchEntity.getHomeClubId()));
        clubService.isClubInactive(clubService.findClubById(matchEntity.getAwayClubId()));
        clubService.isClubInactive(clubService.findClubById(matchEntity.getHomeClubId()));
        clubService.isClubInactive(clubService.findClubById(matchEntity.getAwayClubId()));
        stadiumService.doesStadiumExist(matchEntity.getStadiumId());

        return matchRepository.save(matchEntity);
    }

    public void isEachClubDifferent(MatchEntity matchEntity) {
        if (Objects.equals(matchEntity.getAwayClubId(), matchEntity.getHomeClubId())) {
            throw new ConflictException("Os clubes devem ser diferentes", 400);
        }
    }

    public void stadiumIsFreeForMatchOnDay(Long stadiumId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        if (!matchRepository.findByStadiumAndDate(stadiumId, desiredMatchDate).isEmpty()) {
            throw new ConflictException("O estádio não está livre na data desejada", 409);
        }
    }

    public void checkHomeClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId, desiredMatchDate);
        if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ConflictException("O descanso mínimo para o clube é de 48 horas", 409);
        }
    }

    public void checkAwayClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForAwayClub(homeClubId, desiredMatchDate);
        if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ConflictException("O descanso mínimo para o clube é de 48 horas", 409);
        }
    }

    public MatchEntity updateMatch(Long matchId, MatchEntity requestedToUpdateMatchEntity) {
        matchRepository.findById(matchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        CallCommonValidationForMatch(requestedToUpdateMatchEntity);
        validateIfNewMatchDateIsInTheFuture(requestedToUpdateMatchEntity);
        MatchEntity existingMatchEntity = matchRepository.findById(matchId).get();
        existingMatchEntity.setHomeClubId(requestedToUpdateMatchEntity.getHomeClubId());
        existingMatchEntity.setAwayClubId(requestedToUpdateMatchEntity.getAwayClubId());
        existingMatchEntity.setHomeClubNumberOfGoals(requestedToUpdateMatchEntity.getHomeClubNumberOfGoals());
        existingMatchEntity.setAwayClubNumberOfGoals(requestedToUpdateMatchEntity.getAwayClubNumberOfGoals());
        existingMatchEntity.setMatchDate(requestedToUpdateMatchEntity.getMatchDate());
        existingMatchEntity.setStadiumId(requestedToUpdateMatchEntity.getStadiumId());
        return matchRepository.saveAndFlush(existingMatchEntity);
    }

    public void validateIfNewMatchDateIsInTheFuture(MatchEntity requestedToUpdateMatchEntity) throws ResponseStatusException {
        if (requestedToUpdateMatchEntity.getMatchDate().isAfter(LocalDateTime.now())) {
            throw new ConflictException("A data da partida não pode ser posterior ao dia atual.", 400);
        }
    }

    public void CallCommonValidationForMatch(MatchEntity matchEntity) {
        isEachClubDifferent(matchEntity);
        clubService.wasClubCreatedBeforeGame(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        clubService.wasClubCreatedBeforeGame(matchEntity.getAwayClubId(), matchEntity.getMatchDate());
        clubService.isClubInactive(clubService.findClubById(matchEntity.getHomeClubId()));
        clubService.isClubInactive(clubService.findClubById(matchEntity.getAwayClubId()));
        stadiumIsFreeForMatchOnDay(matchEntity.getStadiumId(), matchEntity.getMatchDate());
        checkHomeClubRestPeriod(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        checkAwayClubRestPeriod(matchEntity.getAwayClubId(), matchEntity.getMatchDate());
    }

    public void deleteMatch(Long matchId) throws ResponseStatusException {
        if (matchRepository.findById(matchId).isEmpty()) {
            throw new ConflictException("A partida não existe na base de dados.", 404);
        }
        matchRepository.deleteById(matchId);
    }

    public MatchEntity getMatchById(Long matchId) throws ResponseStatusException {
        if (matchRepository.findById(matchId).isEmpty()) {
            throw new ConflictException("A partida não existe na base de dados.", 404);
        }
        return matchRepository.findById(matchId).get();
    }

    public Page<MatchEntity> getMatches
            (Long club, Long stadium, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return matchRepository.findByFilters(club, stadium, pageRequest);
    }

}

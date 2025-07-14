package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        isEachClubDifferent(matchEntity);
        ClubEntity homeClubEntity = clubService.findClubById(matchEntity.getHomeClubId());
        ClubEntity awayClubEntity = clubService.findClubById(matchEntity.getAwayClubId());
        clubService.wasClubCreatedBeforeGame(homeClubEntity, awayClubEntity, matchEntity.getMatchDate());
        clubService.isAnyOfClubsInactive(homeClubEntity, awayClubEntity);
        stadiumService.doesStadiumExist(matchEntity.getStadiumId());
        stadiumIsFreeForMatchOnDay(matchEntity.getStadiumId(), matchEntity.getMatchDate());
        checkHomeClubRestPeriod(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        checkAwayClubRestPeriod(matchEntity.getAwayClubId(), matchEntity.getMatchDate());

        return matchRepository.save(matchEntity);
    }

    public void isEachClubDifferent (MatchEntity matchEntity) {
        if (Objects.equals(matchEntity.getAwayClubId(), matchEntity.getHomeClubId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void stadiumIsFreeForMatchOnDay(Long stadiumId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        if (!matchRepository.findByStadiumAndDate(stadiumId, desiredMatchDate).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public void checkHomeClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId, desiredMatchDate);
        if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public void checkAwayClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId, desiredMatchDate);
        if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public MatchEntity updateMatch(Long matchId, MatchEntity requestedToUpdateMatchEntity) {
        isEachClubDifferent(requestedToUpdateMatchEntity);
        matchRepository.findById(matchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MatchEntity existingMatchEntity = matchRepository.findById(matchId).get();
        existingMatchEntity.setHomeClubId(requestedToUpdateMatchEntity.getHomeClubId());
        existingMatchEntity.setAwayClubId(requestedToUpdateMatchEntity.getAwayClubId());
        existingMatchEntity.setHomeClubNumberOfGoals(requestedToUpdateMatchEntity.getHomeClubNumberOfGoals());
        existingMatchEntity.setAwayClubNumberOfGoals(requestedToUpdateMatchEntity.getAwayClubNumberOfGoals());
        existingMatchEntity.setMatchDate(requestedToUpdateMatchEntity.getMatchDate());
        existingMatchEntity.setStadiumId(requestedToUpdateMatchEntity.getStadiumId());
        return matchRepository.saveAndFlush(existingMatchEntity);
    }

}

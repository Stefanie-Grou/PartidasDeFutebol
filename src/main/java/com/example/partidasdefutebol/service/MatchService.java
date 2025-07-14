package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.entities.MatchEntity;
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
        if (Objects.equals(matchEntity.getAwayClubId(), matchEntity.getHomeClubId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ClubEntity homeClubEntity = clubService.findClubById(matchEntity.getHomeClubId());
        ClubEntity awayClubEntity = clubService.findClubById(matchEntity.getAwayClubId());
        clubService.wasClubCreatedBeforeGame(homeClubEntity, awayClubEntity, matchEntity.getMatchDate());
        clubService.isAnyOfClubsInactive(homeClubEntity, awayClubEntity);
        stadiumService.doesStadiumExist(matchEntity.getStadiumId());
        checkHomeClubRestPeriod(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        checkAwayClubRestPeriod(matchEntity.getAwayClubId(), matchEntity.getMatchDate());

        return matchRepository.save(matchEntity);
    }

    public void stadiumIsFreeForMatchOnDay (Long stadiumId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        if (!matchRepository.findByStadiumAndDate(stadiumId, desiredMatchDate).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public void checkHomeClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId,desiredMatchDate);
        if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public void checkAwayClubRestPeriod (Long homeClubId,LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId,desiredMatchDate);
        if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

}

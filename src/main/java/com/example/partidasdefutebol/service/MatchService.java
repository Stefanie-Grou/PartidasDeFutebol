package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.dto.Confrontations;
import com.example.partidasdefutebol.dto.Routs;
import com.example.partidasdefutebol.entities.MatchEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        clubService.wasClubCreatedBeforeGame(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        clubService.wasClubCreatedBeforeGame(matchEntity.getAwayClubId(), matchEntity.getMatchDate());
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

    public Boolean checkHomeClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId, desiredMatchDate);
        if (lastGameForHomeClubWasIn == null) {
            return true;
        } else if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new ConflictException("O descanso mínimo para o clube é de 48 horas", 409);
        }
        return true;
    }

    public Boolean checkAwayClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws ResponseStatusException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForAwayClub(homeClubId, desiredMatchDate);
        if (lastGameForHomeClubWasIn == null) {
            return true;
        } else if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs
                && lastGameForHomeClubWasIn != null) {
            throw new ConflictException("O descanso mínimo para o clube é de 48 horas", 409);
        }
        return true;
    }

    public MatchEntity updateMatch(Long matchId, MatchEntity requestedToUpdateMatchEntity) {
        getMatchById(matchId);
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
        stadiumService.doesStadiumExist(matchEntity.getStadiumId());
        stadiumIsFreeForMatchOnDay(matchEntity.getStadiumId(), matchEntity.getMatchDate());
        checkHomeClubRestPeriod(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        checkAwayClubRestPeriod(matchEntity.getAwayClubId(), matchEntity.getMatchDate());
    }

    public void deleteMatch(Long matchId) throws ResponseStatusException {
        getMatchById(matchId);
        matchRepository.deleteById(matchId);
    }

    public MatchEntity getMatchById(Long matchId) throws ResponseStatusException {
        if (matchRepository.findById(matchId).isEmpty()) {
            throw new ConflictException("A partida não existe na base de dados.", 404);
        }
        return matchRepository.findById(matchId).get();
    }

    public Page<MatchEntity> getMatches
            (Long club, Long stadium, int page, int size, String sortField, String sortOrder,
             Boolean isRout, String showOnly) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return matchRepository.findByFilters(club, stadium, pageRequest, isRout, showOnly);
    }

    public List<Confrontations> getMatchBetweenClubs(Long id1, Long id2) throws ResponseStatusException {
        if (id1.equals(id2)) {
            throw new ConflictException("Os clubes devem ser diferentes", 409);
        }
        clubService.doesClubExist(id1);
        clubService.doesClubExist(id2);
        List<Object[]> matchesBetweenClubs = (List<Object[]>) matchRepository.findMatchesBetweenClubs(id1, id2);
        List<Confrontations> listOfConfrontations = new ArrayList<>();
        for (Object[] matchesBetweenClubsIteration : matchesBetweenClubs) {
            String homeClub = matchesBetweenClubsIteration[0].toString();
            String awayClub = matchesBetweenClubsIteration[1].toString();
            Integer homeClubScoredGoals = Integer.parseInt(matchesBetweenClubsIteration[2].toString());
            Integer awayClubScoredGoals = Integer.parseInt(matchesBetweenClubsIteration[3].toString());
            String winner = matchesBetweenClubsIteration[4].toString();
            Confrontations confrontationEntity = new Confrontations(homeClub, awayClub,
                    homeClubScoredGoals, awayClubScoredGoals, winner);
            listOfConfrontations.add(confrontationEntity);
        }
        return listOfConfrontations;
    }

    public Page<Routs> getAllRouts() {
        List<Object[]> allRouts = matchRepository.getAllRouts();
        List<Routs> matchesWithEqualPlusGoalDiff = new ArrayList<>();
        for (Object[] allRoutsIteration : allRouts) {
            String clubsOnMatch = allRoutsIteration[0].toString();
            String stadiumName = allRoutsIteration[1].toString();
            String matchDate = allRoutsIteration[2].toString();
            Routs routs = new Routs(clubsOnMatch, stadiumName, matchDate);
            matchesWithEqualPlusGoalDiff.add(routs);
        }
        return new PageImpl<>(matchesWithEqualPlusGoalDiff);
    }
}

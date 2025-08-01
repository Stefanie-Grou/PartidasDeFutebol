package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.dto.ConfrontationsDTO;
import com.example.partidasdefutebol.dto.RoutsDTO;
import com.example.partidasdefutebol.entities.Match;
import com.example.partidasdefutebol.exceptions.CustomException;
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

    public Match createMatch(Match matchEntity) {
        CallCommonValidationForMatch(matchEntity);
        clubService.wasClubCreatedBeforeGame(matchEntity.getHomeClubId(), matchEntity.getMatchDate());
        clubService.wasClubCreatedBeforeGame(matchEntity.getAwayClubId(), matchEntity.getMatchDate());
        clubService.isClubInactive(clubService.findClubById(matchEntity.getHomeClubId()));
        clubService.isClubInactive(clubService.findClubById(matchEntity.getAwayClubId()));
        stadiumService.doesStadiumExist(matchEntity.getStadiumId());

        return matchRepository.save(matchEntity);
    }

    public void isEachClubDifferent(Match matchEntity) throws CustomException {
        if (Objects.equals(matchEntity.getAwayClubId(), matchEntity.getHomeClubId())) {
            throw new CustomException("Os clubes devem ser diferentes", 400);
        }
    }

    public void stadiumIsFreeForMatchOnDay(Long stadiumId, LocalDateTime desiredMatchDate) throws CustomException {
        if (!matchRepository.findByStadiumAndDate(stadiumId, desiredMatchDate).isEmpty()) {
            throw new CustomException("O estádio não está livre na data desejada", 409);
        }
    }

    public Boolean checkHomeClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws CustomException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForHomeClub(homeClubId, desiredMatchDate);
        if (lastGameForHomeClubWasIn == null) {
            return true;
        } else if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs) {
            throw new CustomException("O descanso mínimo para o clube é de 48 horas", 409);
        }
        return true;
    }

    public Boolean checkAwayClubRestPeriod(Long homeClubId, LocalDateTime desiredMatchDate) throws CustomException {
        final int restPeriodInHoursIs = 48;
        LocalDateTime lastGameForHomeClubWasIn = matchRepository.hoursSinceLastGameForAwayClub(homeClubId, desiredMatchDate);
        if (lastGameForHomeClubWasIn == null) {
            return true;
        } else if (Duration.between(lastGameForHomeClubWasIn, desiredMatchDate).toHours() < restPeriodInHoursIs
                && lastGameForHomeClubWasIn != null) {
            throw new CustomException("O descanso mínimo para o clube é de 48 horas", 409);
        }
        return true;
    }

    public Match updateMatch(Long matchId, Match requestedToUpdateMatchEntity) {
        getMatchById(matchId);
        CallCommonValidationForMatch(requestedToUpdateMatchEntity);
        validateIfNewMatchDateIsInTheFuture(requestedToUpdateMatchEntity);
        Match existingMatchEntity = matchRepository.findById(matchId).get();
        existingMatchEntity.setHomeClubId(requestedToUpdateMatchEntity.getHomeClubId());
        existingMatchEntity.setAwayClubId(requestedToUpdateMatchEntity.getAwayClubId());
        existingMatchEntity.setHomeClubNumberOfGoals(requestedToUpdateMatchEntity.getHomeClubNumberOfGoals());
        existingMatchEntity.setAwayClubNumberOfGoals(requestedToUpdateMatchEntity.getAwayClubNumberOfGoals());
        existingMatchEntity.setMatchDate(requestedToUpdateMatchEntity.getMatchDate());
        existingMatchEntity.setStadiumId(requestedToUpdateMatchEntity.getStadiumId());
        return matchRepository.saveAndFlush(existingMatchEntity);
    }

    public void validateIfNewMatchDateIsInTheFuture(Match requestedToUpdateMatchEntity) throws CustomException {
        if (requestedToUpdateMatchEntity.getMatchDate().isAfter(LocalDateTime.now())) {
            throw new CustomException("A data da partida não pode ser posterior ao dia atual.", 400);
        }
    }

    public void CallCommonValidationForMatch(Match matchEntity) {
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

    public Match getMatchById(Long matchId) throws ResponseStatusException {
        if (matchRepository.findById(matchId).isEmpty()) {
            throw new CustomException("A partida não existe na base de dados.", 404);
        }
        return matchRepository.findById(matchId).get();
    }

    public Page<Match> getMatches
            (Long club, Long stadium, int page, int size, String sortField, String sortOrder,
             Boolean isRout, String showOnly) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return matchRepository.findByFilters(club, stadium, pageRequest, isRout, showOnly);
    }

    public List<ConfrontationsDTO> getMatchBetweenClubs(Long id1, Long id2) throws ResponseStatusException {
        if (id1.equals(id2)) {
            throw new CustomException("Os clubes devem ser diferentes", 409);
        }
        clubService.doesClubExist(id1);
        clubService.doesClubExist(id2);
        List<Object[]> matchesBetweenClubs = (List<Object[]>) matchRepository.findMatchesBetweenClubs(id1, id2);
        List<ConfrontationsDTO> listOfConfrontations = new ArrayList<>();
        for (Object[] matchesBetweenClubsIteration : matchesBetweenClubs) {
            String homeClub = matchesBetweenClubsIteration[0].toString();
            String awayClub = matchesBetweenClubsIteration[1].toString();
            Integer homeClubScoredGoals = Integer.parseInt(matchesBetweenClubsIteration[2].toString());
            Integer awayClubScoredGoals = Integer.parseInt(matchesBetweenClubsIteration[3].toString());
            String winner = matchesBetweenClubsIteration[4].toString();
            ConfrontationsDTO confrontationEntity = new ConfrontationsDTO(homeClub, awayClub,
                    homeClubScoredGoals, awayClubScoredGoals, winner);
            listOfConfrontations.add(confrontationEntity);
        }
        return listOfConfrontations;
    }

    public Page<RoutsDTO> getAllRouts() {
        List<Object[]> allRouts = matchRepository.getAllRouts();
        List<RoutsDTO> matchesWithEqualPlusGoalDiff = new ArrayList<>();
        for (Object[] allRoutsIteration : allRouts) {
            String clubsOnMatch = allRoutsIteration[0].toString();
            String stadiumName = allRoutsIteration[1].toString();
            String matchDate = allRoutsIteration[2].toString();
            RoutsDTO routs = new RoutsDTO(clubsOnMatch, stadiumName, matchDate);
            matchesWithEqualPlusGoalDiff.add(routs);
        }
        return new PageImpl<>(matchesWithEqualPlusGoalDiff);
    }
}

package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.dto.GoalSummaryDTO;
import com.example.partidasdefutebol.dto.SummaryByOpponentDTO;
import com.example.partidasdefutebol.entities.Club;
import com.example.partidasdefutebol.dto.RankingDTO;
import com.example.partidasdefutebol.exceptions.CustomException;
import com.example.partidasdefutebol.repository.ClubRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;

    public void createClub(Club clubEntity) {
        clubRepository.save(clubEntity);
    }

    public void doesClubExist(Long clubId) throws CustomException {
        if (!clubRepository.existsById(clubId)) {
            throw new AmqpRejectAndDontRequeueException("Clube " + clubId + " não encontrado na base de dados.");
        }
    }

    public void updateClub(Long id, Club requestedToUpdateClubEntity) {
        doesClubExist(id);
        wasClubCreatedAfterGame(id, requestedToUpdateClubEntity);
        Club clubEntity = clubRepository.findById(id).get();
        clubEntity.setName(requestedToUpdateClubEntity.getName());
        clubEntity.setStateAcronym(requestedToUpdateClubEntity.getStateAcronym());
        clubEntity.setCreatedOn(requestedToUpdateClubEntity.getCreatedOn());
        clubEntity.setIsActive(requestedToUpdateClubEntity.getIsActive());
        clubRepository.saveAndFlush(clubEntity);
    }

    public Club deleteClub(Long id) {
        Club existingClubEntity = clubRepository.findById(id).get();
        existingClubEntity.setIsActive(false);
        return clubRepository.save(existingClubEntity);
    }

    public Club findClubById(Long id) {
        doesClubExist(id);
        return clubRepository.findById(id).get();
    }

    public Page<Club> getClubs(String name, String state, Boolean isActive, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return clubRepository.findByFilters(name, state, isActive, pageRequest);
    }

    public void wasClubCreatedBeforeGame(Long clubid,
                                         LocalDateTime matchDate) {
        if (!clubRepository.findById(clubid).get().getCreatedOn().isBefore(ChronoLocalDate.from(matchDate))) {
            throw new AmqpRejectAndDontRequeueException("A data de criação do clube " +
                    clubRepository.findById(clubid).get().getName() +
                    " deve ser anterior ao registro de alguma partida cadastrada.");
        }
    }

    public void isClubInactive(Club clubEntity) throws ResponseStatusException {
        if (!clubEntity.getIsActive()) {
            throw new CustomException("O clube " + clubEntity.getName() + " está inativo", 409);
        }
    }

    public void wasClubCreatedAfterGame(Long clubId, Club clubEntity) throws CustomException {
        if (!clubRepository.wasClubCreatedAfterGame(clubEntity.getCreatedOn().atStartOfDay(), clubId)) {
            String message = "A nova data de criação do clube está posterior ao registro de alguma partida cadastrada.\nClube: {} Nova data de criação: {}\n{}";
            throw new CustomException(message);
        }
    }

    public GoalSummaryDTO getClubRetrospective(Long id) throws ResponseStatusException {
        List matchResultsByClub = clubRepository.findMatchResultsByClubId(id);
        Integer positiveGoals = clubRepository.findTotalPositiveGoalsByClubId(id);
        Integer negativeGoals = clubRepository.findTotalNegativeGoalsByClubId(id);
        Integer totalOfVictories = Collections.frequency(matchResultsByClub, "vitória");
        Integer totalOfDraws = Collections.frequency(matchResultsByClub, "empate");
        Integer totalOfDefeats = Collections.frequency(matchResultsByClub, "derrota");
        return new GoalSummaryDTO(positiveGoals, negativeGoals, totalOfVictories, totalOfDraws, totalOfDefeats);
    }

    public Page<SummaryByOpponentDTO> getClubRetrospectiveByOpponent(Long id) throws ResponseStatusException {
        List<Object[]> matchResultsByClub = clubRepository.findClubRetrospectiveByIdAndOpponent(id);
        List<SummaryByOpponentDTO> summaryList = new ArrayList<>();
        for (Object[] matchResultByClub : matchResultsByClub) {
            String opponent = matchResultByClub[0].toString();
            Integer totalOfMatches = Integer.parseInt(matchResultByClub[1].toString());
            Integer totalOfMatchesWon = Integer.parseInt(matchResultByClub[2].toString());
            Integer totalOfMatchesDrawn = Integer.parseInt(matchResultByClub[3].toString());
            Integer totalOfMatchesLost = Integer.parseInt(matchResultByClub[4].toString());
            Integer scoredGoals = Integer.parseInt(matchResultByClub[5].toString());
            Integer concededGoals = Integer.parseInt(matchResultByClub[6].toString());
            SummaryByOpponentDTO summaryByOpponent = new SummaryByOpponentDTO(opponent, totalOfMatches,
                    totalOfMatchesWon, totalOfMatchesDrawn, totalOfMatchesLost, scoredGoals, concededGoals);
            summaryList.add(summaryByOpponent);
        }
        return new PageImpl<>(summaryList);
    }

    public List<RankingDTO> callClubRankingDispatcher(String rankingFactorFromController) {
        List<Object[]> returnedRankingFromDatabase;
        switch (rankingFactorFromController) {
            case "partidas":
                returnedRankingFromDatabase = fetchClubRankingByNumberOfMatchesData();
                break;
            case "vitorias":
                returnedRankingFromDatabase = fetchClubRankingByTotalOfVictoriesData();
                break;
            case "gols":
                returnedRankingFromDatabase = fetchClubRankingByTotalOfGoalsData();
                break;
            case "pontos":
                returnedRankingFromDatabase = fetchClubRankingByTotalOfPointsData();
                break;
            default:
                throw new AmqpRejectAndDontRequeueException("Fator de classificação inválido");
        }
        return setReturnedRankingInfoIntoEntity(returnedRankingFromDatabase);
    }

    public List<Object[]> fetchClubRankingByNumberOfMatchesData() {
        return clubRepository.getRankingByTotalMatches();
    }

    public List<Object[]> fetchClubRankingByTotalOfVictoriesData() {
        return clubRepository.getRankingByTotalWins();
    }

    public List<Object[]> fetchClubRankingByTotalOfGoalsData() {
        return clubRepository.getRankingByTotalGoals();
    }

    public List<Object[]> fetchClubRankingByTotalOfPointsData() {
        return clubRepository.getRankingByTotalPoints();
    }

    public List<RankingDTO> setReturnedRankingInfoIntoEntity(List<Object[]> returnedRankingFromDatabase) {
        List<RankingDTO> rankingList = new ArrayList<>();
        for (Object[] objects : returnedRankingFromDatabase) {
            RankingDTO ranking = new RankingDTO(objects[0].toString(),
                    Integer.parseInt(objects[1].toString()));
            rankingList.add(ranking);
        }
        return rankingList;
    }
}
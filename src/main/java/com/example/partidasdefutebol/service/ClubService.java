package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.dto.GoalSummary;
import com.example.partidasdefutebol.dto.SummaryByOpponent;
import com.example.partidasdefutebol.entities.ClubEntity;
import com.example.partidasdefutebol.exceptions.ConflictException;
import com.example.partidasdefutebol.repository.ClubRepository;
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

import static com.example.partidasdefutebol.util.isValidBrazilianState.isValidBrazilianState;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    public ClubEntity createClub(ClubEntity clubEntity) {
        if (!isValidBrazilianState(clubEntity.getStateAcronym())) {
            throw new ConflictException("A sigla do estado é inválida.", 409);
        }
        return clubRepository.save(clubEntity);
    }

    public void doesClubExist(Long clubId) throws ResponseStatusException {
        if (!clubRepository.existsById(clubId)) {
            throw new ConflictException("Clube " + clubId + " não encontrado na base de dados.", 404);
        }
    }

    public ClubEntity updateClub(Long id, ClubEntity requestedToUpdateClubEntity) {
        doesClubExist(id);
        wasClubCreatedAfterGame(id, requestedToUpdateClubEntity);
        ClubEntity clubEntity = clubRepository.findById(id).get();
        clubEntity.setClubName(requestedToUpdateClubEntity.getClubName());
        clubEntity.setStateAcronym(requestedToUpdateClubEntity.getStateAcronym());
        clubEntity.setCreatedOn(requestedToUpdateClubEntity.getCreatedOn());
        clubEntity.setIsActive(requestedToUpdateClubEntity.getIsActive());
        return clubRepository.saveAndFlush(clubEntity);
    }

    public ClubEntity deleteClub(Long id) {
        doesClubExist(id);
        ClubEntity existingClubEntity = clubRepository.findById(id).get();
        existingClubEntity.setIsActive(false);
        return clubRepository.save(existingClubEntity);
    }

    public ClubEntity findClubById(Long id) {
        doesClubExist(id);
        return clubRepository.findById(id).get();
    }

    public Page<ClubEntity> getClubs(String name, String state, Boolean isActive, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortField);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return clubRepository.findByFilters(name, state, isActive, pageRequest);
    }

    public void wasClubCreatedBeforeGame(Long clubid,
                                         LocalDateTime matchDate) {
        if (clubRepository.findById(clubid).get().getCreatedOn().isAfter(ChronoLocalDate.from(matchDate))) {
            throw new ConflictException("A data de criação do clube " + clubRepository.findById(clubid).get().getClubName() +
                    " deve ser anterior ao registro de alguma partida cadastrada.", 409);
        }
    }

    public void isClubInactive(ClubEntity clubEntity) {
        if (!clubEntity.getIsActive()) {
            throw new ConflictException("O clube " + clubEntity.getClubName() + " está inativo", 409);
        }
    }

    public void wasClubCreatedAfterGame(Long clubId, ClubEntity clubEntity) throws ResponseStatusException {
        Boolean isClubUpToUpdateCreatedOn = clubRepository.wasClubCreatedAfterGame(
                clubEntity.getCreatedOn().atStartOfDay(), clubId);
        if (!isClubUpToUpdateCreatedOn) {
            throw new ConflictException("A nova data de criação do clube está posterior ao registro de alguma partida cadastrada.", 409);
        }
    }

    public GoalSummary getClubRetrospective(Long id) throws ResponseStatusException {
        doesClubExist(id);
        List matchResultsByClub = clubRepository.findMatchResultsByClubId(id);
        Integer positiveGoals = clubRepository.findTotalPositiveGoalsByClubId(id);
        Integer negativeGoals = clubRepository.findTotalNegativeGoalsByClubId(id);
        Integer totalOfVictories = Collections.frequency(matchResultsByClub, "vitória");
        Integer totalOfDraws = Collections.frequency(matchResultsByClub, "empate");
        Integer totalOfDefeats = Collections.frequency(matchResultsByClub, "derrota");
        return new GoalSummary(positiveGoals, negativeGoals, totalOfVictories, totalOfDraws, totalOfDefeats);
    }

    public Page<SummaryByOpponent> getClubRetrospectiveByOpponent(Long id) throws ResponseStatusException {
        doesClubExist(id);
        List<Object[]> matchResultsByClub = clubRepository.findClubRetrospectiveByIdAndOpponent(id);
        List<SummaryByOpponent> summaryList = new ArrayList<>();
        for (Object[] matchResultByClub : matchResultsByClub) {
            String opponent = matchResultByClub[0].toString();
            Integer totalOfMatches = Integer.parseInt(matchResultByClub[1].toString());
            Integer totalOfMatchesWon = Integer.parseInt(matchResultByClub[2].toString());
            Integer totalOfMatchesDrawn = Integer.parseInt(matchResultByClub[3].toString());
            Integer totalOfMatchesLost = Integer.parseInt(matchResultByClub[4].toString());
            Integer scoredGoals = Integer.parseInt(matchResultByClub[5].toString());
            Integer concededGoals = Integer.parseInt(matchResultByClub[6].toString());
            SummaryByOpponent summaryByOpponent = new SummaryByOpponent(opponent, totalOfMatches,
                    totalOfMatchesWon, totalOfMatchesDrawn, totalOfMatchesLost, scoredGoals, concededGoals);
            summaryList.add(summaryByOpponent);
        }
        return new PageImpl<>(summaryList);
    }
}
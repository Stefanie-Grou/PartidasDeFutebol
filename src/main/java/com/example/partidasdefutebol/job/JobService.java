package com.example.partidasdefutebol.job;

import com.example.partidasdefutebol.dto.RankingDTO;
import com.example.partidasdefutebol.service.ClubService;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    private final ClubService clubService;

    public JobService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Job
    public void printRankingWithJob() {

        List<RankingDTO> ranking = clubService.callClubRankingDispatcher("pontos");
        System.out.println("\n\n\nRanking atualizado em " + LocalDateTime.now() + "\n\n\n" + ranking);
    }
}

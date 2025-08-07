package com.example.partidasdefutebol.job;

import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobService {

    @Job
    public void printRankingWithJob() {
        System.out.println("\n\n\nPrint ranking with job " + LocalDateTime.now());
    }
}

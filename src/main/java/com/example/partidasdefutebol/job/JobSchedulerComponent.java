package com.example.partidasdefutebol.job;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class JobSchedulerComponent {

    private final JobScheduler jobScheduler;

    @Autowired
    public JobSchedulerComponent(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @Scheduled(fixedRate = 86400000)
    public void scheduleJob() {
        jobScheduler.enqueue(JobService::printRankingWithJob);
    }
}

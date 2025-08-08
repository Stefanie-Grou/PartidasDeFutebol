package com.example.partidasdefutebol.job;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
//todo: checar o funcionamento do job, para que ele se execute periodicamente
public class JobSchedulerComponent {

    private final JobScheduler jobScheduler;

    @Autowired
    public JobSchedulerComponent(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    public static final Long dayInMilliseconds = 86400000L;
    @Scheduled(fixedRate = dayInMilliseconds)
    public void scheduleJob() {
        jobScheduler.enqueue(JobService::printRankingWithJob);
    }
}

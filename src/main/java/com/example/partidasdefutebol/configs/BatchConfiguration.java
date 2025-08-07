package com.example.partidasdefutebol.configs;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
/*
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Bean
    public BatchProperties.Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("rankingConsolePrinterJob")
                .incrementer(new RunIdIncrementer())
                .flow(printerStep(stepBuilderFactory))
                .end()
                .build();
    }

    @Bean
    public Step printerStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("printerStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Printing batch job " + LocalDateTime.now());
                    return null;
                })
                .build();
    }
}


 */
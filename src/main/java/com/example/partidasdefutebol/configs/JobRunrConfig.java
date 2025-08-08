package com.example.partidasdefutebol.configs;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.storage.StorageProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunrConfig {

    @Bean
    public org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult jobRunrConfiguration
            (StorageProvider storageProvider, ApplicationContext applicationContext) {
        return JobRunr.configure()
                .useStorageProvider(storageProvider)
                .useJobActivator(applicationContext :: getBean)
                .useBackgroundJobServer()
                .initialize();
    }
}

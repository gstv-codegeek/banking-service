package com.silentowl.banking_app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new ApplicationAuditorAware();
    }


}

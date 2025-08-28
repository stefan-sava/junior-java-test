package com.example.carins.startup;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class PolicyEndDateBackfill {
    private static final Logger log = LoggerFactory.getLogger(PolicyEndDateBackfill.class);

    @Bean
    ApplicationRunner backfillEndDates(InsurancePolicyRepository repo) {
        return args -> {
            List<InsurancePolicy> missing = repo.findByEndDateIsNull();
            if (missing.isEmpty()) return;

            for (InsurancePolicy p : missing) {
                LocalDate start = p.getStartDate() != null ? p.getStartDate() : LocalDate.now();
                p.setStartDate(start);
                p.setEndDate(start.plusYears(1));
            }
            repo.saveAll(missing);
            log.info("Backfilled {} policies with missing endDate (startDate + 1 year)", missing.size());
        };
    }
}

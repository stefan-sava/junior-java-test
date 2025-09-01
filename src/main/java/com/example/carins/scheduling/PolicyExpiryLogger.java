package com.example.carins.scheduling;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PolicyExpiryLogger {

    private static final Logger log = LoggerFactory.getLogger(PolicyExpiryLogger.class);
    private static final Duration WINDOW = Duration.ofHours(1);

    private final InsurancePolicyRepository repo;


    private final Set<Long> loggedToday = ConcurrentHashMap.newKeySet();
    private LocalDate markerDate = LocalDate.now(ZoneId.systemDefault());

    public PolicyExpiryLogger(InsurancePolicyRepository repo) {
        this.repo = repo;
    }

    @Scheduled(fixedRate = 60_000)
    public void run() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalTime now = LocalTime.now(zone);


        if (!today.equals(markerDate)) {
            loggedToday.clear();
            markerDate = today;
        }

        if (now.isAfter(LocalTime.MIDNIGHT.plus(WINDOW))) {
            return;
        }

        LocalDate expiredYesterday = today.minusDays(1);
        List<InsurancePolicy> expired = repo.findByEndDate(expiredYesterday);
        if (expired.isEmpty()) return;

        for (InsurancePolicy p : expired) {
            Long id = p.getId();
            if (id == null) continue;


            if (loggedToday.add(id)) {
                Long carId = (p.getCar() != null ? p.getCar().getId() : null);
                log.info("Policy {} for car {} expired on {}", id, carId, p.getEndDate());
            }
        }
    }
}

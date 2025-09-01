    package com.example.carins.service;

    import com.example.carins.model.InsurancePolicy;
    import com.example.carins.model.InsuranceClaim;
    import com.example.carins.repo.CarRepository;
    import com.example.carins.repo.InsurancePolicyRepository;
    import com.example.carins.repo.InsuranceClaimRepository;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.util.*;

    @Service
    public class HistoryService {

        private final CarRepository carRepository;
        private final InsurancePolicyRepository policyRepository;
        private final InsuranceClaimRepository claimRepository;

        public HistoryService(
                CarRepository carRepository,
                InsurancePolicyRepository policyRepository,
                InsuranceClaimRepository claimRepository) {
            this.carRepository = carRepository;
            this.policyRepository = policyRepository;
            this.claimRepository = claimRepository;
        }

        public boolean carExists(Long carId) {
            return carRepository.existsById(carId);
        }

        public List<HistoryEvent> getCarHistory(Long carId) {
            if (!carExists(carId)) {
                throw new java.util.NoSuchElementException("Car not found: " + carId);
            }

            List<HistoryEvent> events = new ArrayList<>();



            List<InsurancePolicy> policies = policyRepository.findAllByCarIdOrderByStartDateAsc(carId);


            for (var p : policies) {
                events.add(new HistoryEvent(
                        p.getStartDate(),
                        "POLICY_START",
                        "Policy started",
                        java.util.Map.of(
                                "policyId", String.valueOf(p.getId()),
                                "provider", String.valueOf(p.getProvider())
                        )
                ));
                if (p.getEndDate() != null) {
                    events.add(new HistoryEvent(
                            p.getEndDate(),
                            "POLICY_END",
                            "Policy ended",
                            java.util.Map.of(
                                    "policyId", String.valueOf(p.getId()),
                                    "provider", String.valueOf(p.getProvider())
                            )
                    ));
                }
            }



            List<InsuranceClaim> claims = claimRepository.findByCarIdOrderByClaimDateAsc(carId);
            for (InsuranceClaim c : claims) {
                events.add(new HistoryEvent(
                        c.getClaimDate(),
                        "CLAIM",
                        c.getDescription(),
                        Map.of(
                                "claimId", String.valueOf(c.getId()),
                                "amount", c.getAmount().toPlainString()
                        )
                ));
            }


            events.sort(Comparator
                    .comparing(HistoryEvent::date)
                    .thenComparing(e -> switch (e.type()) {
                        case "POLICY_START" -> 0;
                        case "CLAIM" -> 1;
                        case "POLICY_END" -> 2;
                        default -> 3;
                    }));

            return events;
        }

        public record HistoryEvent(LocalDate date, String type, String summary, Map<String, String> data) { }
    }

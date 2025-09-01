package com.example.carins.web.dto;

import java.time.LocalDate;
import java.util.Map;

public record HistoryEventDto(
        LocalDate date,
        String type,
        String summary,
        Map<String, String> data
) {}

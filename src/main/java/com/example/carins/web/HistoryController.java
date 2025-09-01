package com.example.carins.web;

import com.example.carins.service.HistoryService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars/{carId}/history")
public class HistoryController {
    private final HistoryService history;

    public HistoryController(HistoryService history) { this.history = history; }

    @GetMapping
    public ResponseEntity<?> get(@PathVariable Long carId) {
        try {
            return ResponseEntity.ok(history.getCarHistory(carId));
        } catch (java.util.NoSuchElementException nf) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(nf.getMessage()));
        }
    }

    public record Error(String message) {}
}

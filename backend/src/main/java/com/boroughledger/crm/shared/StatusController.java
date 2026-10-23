package com.boroughledger.crm.shared;

import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {
    @GetMapping
    public Status status() { return new Status("BOROUGH & LEDGER", "1.0.0", "online", Instant.now()); }
    public record Status(String product, String version, String state, Instant timestamp) { }
}

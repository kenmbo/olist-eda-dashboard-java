package com.olist.dashboard.parity;

import java.util.List;

/** Result of comparing one endpoint's successful FastAPI and Spring responses. */
public record EndpointParityResult(String endpoint, List<ParityMismatch> mismatches) {

    public EndpointParityResult {
        mismatches = List.copyOf(mismatches);
    }

    public boolean matches() {
        return mismatches.isEmpty();
    }
}

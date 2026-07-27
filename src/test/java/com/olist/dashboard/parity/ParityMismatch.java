package com.olist.dashboard.parity;

/** A semantic difference between FastAPI and Spring JSON at a precise JSON path. */
public record ParityMismatch(String jsonPath, String detail) {
}

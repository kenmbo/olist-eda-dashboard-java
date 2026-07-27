package com.olist.dashboard.error;

/** Stable, sanitized JSON body returned for failed API requests. */
public record ApiErrorResponse(String error) {
}

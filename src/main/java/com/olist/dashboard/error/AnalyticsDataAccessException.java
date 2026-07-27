package com.olist.dashboard.error;

/**
 * Wraps a repository failure after the repository has recorded any operator-facing diagnostic.
 *
 * <p>The exception message may contain SQL or driver detail and must never be returned to an API
 * client.</p>
 */
public class AnalyticsDataAccessException extends RuntimeException {

    public AnalyticsDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalyticsDataAccessException(String message) {
        super(message);
    }
}

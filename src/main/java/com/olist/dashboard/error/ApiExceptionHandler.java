package com.olist.dashboard.error;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts server-side failures to stable non-2xx JSON without leaking SQL, paths, or driver text.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<ApiErrorResponse> databaseUnavailable(CannotGetJdbcConnectionException exception) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, "The analytics database is unavailable.");
    }

    @ExceptionHandler({AnalyticsDataAccessException.class, DataAccessException.class})
    public ResponseEntity<ApiErrorResponse> analyticsQueryFailure(RuntimeException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "The analytics query could not be completed.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> unexpectedFailure(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");
    }

    private static ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(message));
    }
}

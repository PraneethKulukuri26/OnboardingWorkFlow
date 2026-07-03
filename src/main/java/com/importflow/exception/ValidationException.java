package com.importflow.exception;

import java.util.List;

/**
 * Thrown when validation fails at a pipeline level (not per-record).
 */
public class ValidationException extends ImportException {

    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message + " → " + errors);
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}

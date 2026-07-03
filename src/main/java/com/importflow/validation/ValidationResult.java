package com.importflow.validation;

import java.util.Collections;
import java.util.List;

/**
 * The result of applying one or more validation rules to a single record.
 */
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    /**
     * Create a successful validation result.
     */
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Create a failed validation result with error messages.
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, List.copyOf(errors));
    }

    /**
     * Create a failed validation result with a single error message.
     */
    public static ValidationResult failure(String error) {
        return new ValidationResult(false, List.of(error));
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return valid ? "VALID" : "INVALID" + errors;
    }
}

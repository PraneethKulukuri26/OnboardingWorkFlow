package com.importflow.validation;

/**
 * A single validation rule applied to a domain object.
 *
 * <p>Implementations check one specific constraint (e.g., email format,
 * salary range, mandatory fields). Multiple rules are composed via
 * {@link CompositeValidator}.</p>
 *
 * @param <T> the domain type being validated
 */
@FunctionalInterface
public interface ValidationRule<T> {

    /**
     * Validate the given record.
     *
     * @param record the parsed domain object to validate
     * @return a {@link ValidationResult} indicating success or failure with errors
     */
    ValidationResult validate(T record);

    /**
     * Human-readable name of this rule, used in error reports.
     */
    default String ruleName() {
        return getClass().getSimpleName();
    }
}

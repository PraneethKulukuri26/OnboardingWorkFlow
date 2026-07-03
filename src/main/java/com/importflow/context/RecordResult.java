package com.importflow.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks the outcome of processing a single record through the pipeline.
 *
 * <p>Each record starts as {@link Status#PENDING}. Stages update the status
 * to {@link Status#SUCCESS} or {@link Status#FAILED} and may attach
 * error messages or warnings.</p>
 *
 * @param <T> the domain type (e.g., Employee)
 */
public class RecordResult<T> {

    /**
     * The lifecycle status of a record in the pipeline.
     */
    public enum Status {
        PENDING,
        SUCCESS,
        FAILED,
        SKIPPED,
        DUPLICATE
    }

    private final int rowNumber;
    private final RawRecord rawRecord;
    private T parsedObject;
    private Status status;
    private final List<String> errors;
    private final List<String> warnings;

    public RecordResult(int rowNumber, RawRecord rawRecord) {
        this.rowNumber = rowNumber;
        this.rawRecord = rawRecord;
        this.status = Status.PENDING;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────────────────

    public int getRowNumber() {
        return rowNumber;
    }

    public RawRecord getRawRecord() {
        return rawRecord;
    }

    public T getParsedObject() {
        return parsedObject;
    }

    public Status getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public boolean isFailed() {
        return status == Status.FAILED || status == Status.DUPLICATE;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    // ── Mutators (used by stages) ──────────────────────────────────────

    public void setParsedObject(T parsedObject) {
        this.parsedObject = parsedObject;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.status = Status.FAILED;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public void markSuccess() {
        if (!isFailed()) {
            this.status = Status.SUCCESS;
        }
    }

    public void markDuplicate(String reason) {
        this.errors.add(reason);
        this.status = Status.DUPLICATE;
    }

    @Override
    public String toString() {
        return "RecordResult{row=" + rowNumber + ", status=" + status
                + ", errors=" + errors + "}";
    }
}

package com.importflow.stage;

/**
 * Marker interface for stages that validate parsed domain objects
 * and update {@code RecordResult} entries with validation errors.
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface ValidateStage<T> extends Stage<T> {
}

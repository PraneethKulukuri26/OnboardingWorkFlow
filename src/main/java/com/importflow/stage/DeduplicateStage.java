package com.importflow.stage;

/**
 * Marker interface for stages that detect and flag duplicate
 * records within the current batch.
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface DeduplicateStage<T> extends Stage<T> {
}

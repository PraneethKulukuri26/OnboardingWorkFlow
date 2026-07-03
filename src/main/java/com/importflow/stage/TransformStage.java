package com.importflow.stage;

/**
 * Marker interface for stages that transform or normalize
 * parsed domain objects (e.g., trim whitespace, uppercase fields).
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface TransformStage<T> extends Stage<T> {
}

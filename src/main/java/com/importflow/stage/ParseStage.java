package com.importflow.stage;

/**
 * Marker interface for stages that parse raw records into
 * typed domain objects and populate {@code context.getParsedRecords()}.
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface ParseStage<T> extends Stage<T> {
}

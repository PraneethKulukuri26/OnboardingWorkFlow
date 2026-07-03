package com.importflow.stage;

/**
 * Marker interface for stages that read raw data from a source
 * (CSV file, Excel, database, API, etc.) and populate
 * {@code context.getRawRecords()}.
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface ReadStage<T> extends Stage<T> {
}

package com.importflow.stage;

/**
 * Marker interface for stages that generate reports
 * (console, file, email, etc.) from the import results.
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface ReportStage<T> extends Stage<T> {
}

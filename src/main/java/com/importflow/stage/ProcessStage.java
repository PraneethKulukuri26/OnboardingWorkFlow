package com.importflow.stage;

/**
 * Marker interface for stages that perform custom business-logic
 * processing on validated records.
 *
 * @param <T> the domain type flowing through the pipeline
 */
public interface ProcessStage<T> extends Stage<T> {
}

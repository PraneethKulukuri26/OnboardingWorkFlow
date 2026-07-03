package com.importflow.stage;

import com.importflow.context.ImportContext;

/**
 * The fundamental unit of work in the ImportFlow pipeline.
 *
 * <p>Every operation — reading, parsing, validating, transforming, storing,
 * reporting — is a Stage. Stages are composable and executed sequentially
 * by the pipeline executor.</p>
 *
 * @param <T> the domain type flowing through the pipeline (e.g., Employee)
 */
@FunctionalInterface
public interface Stage<T> {

    /**
     * Execute this stage's logic against the shared import context.
     *
     * @param context the mutable context that carries all pipeline state
     */
    void execute(ImportContext<T> context);

    /**
     * Human-readable name for logging and reporting.
     * Defaults to the simple class name.
     */
    default String name() {
        return getClass().getSimpleName();
    }
}

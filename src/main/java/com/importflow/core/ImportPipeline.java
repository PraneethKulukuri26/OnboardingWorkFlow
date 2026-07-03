package com.importflow.core;

import com.importflow.context.ImportContext;
import com.importflow.context.RecordResult;
import com.importflow.exception.ImportException;
import com.importflow.report.ImportReport;
import com.importflow.repository.Repository;
import com.importflow.stage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The central orchestrator of ImportFlow.
 *
 * <p>Provides a fluent builder API for assembling a pipeline of stages,
 * and an executor that runs them sequentially against a shared
 * {@link ImportContext}.</p>
 *
 * <h3>Usage:</h3>
 * <pre>{@code
 * ImportReport<Employee> report = ImportPipeline.<Employee>builder()
 *     .read(csvReader)
 *     .parse(employeeParser)
 *     .validate(employeeValidator)
 *     .transform(normalizer)
 *     .deduplicate(duplicateChecker)
 *     .store(repository)
 *     .report(consoleReporter)
 *     .build()
 *     .execute(Map.of("file.path", "employees.csv"));
 * }</pre>
 *
 * <p>Internally, the pipeline is simply a {@code List<Stage<T>>}.
 * The executor loops through them:</p>
 * <pre>{@code
 * for (Stage<T> stage : stages) {
 *     stage.execute(context);
 * }
 * }</pre>
 *
 * @param <T> the domain type flowing through the pipeline
 */
public class ImportPipeline<T> {

    private final List<Stage<T>> stages;

    private ImportPipeline(List<Stage<T>> stages) {
        this.stages = List.copyOf(stages);
    }

    /**
     * Create a new pipeline builder.
     *
     * @param <T> the domain type
     * @return a fresh builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Execute the pipeline with the given configuration.
     *
     * @param config key-value configuration (e.g., "file.path" → "employees.csv")
     * @return an {@link ImportReport} summarizing the import
     */
    public ImportReport<T> execute(Map<String, String> config) {
        ImportContext<T> context = new ImportContext<>(config);

        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│       ImportFlow Pipeline — Starting        │");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.println();

        for (Stage<T> stage : stages) {
            String stageName = stage.name();
            System.out.println("  ▶ Executing stage: " + stageName);

            try {
                stage.execute(context);
            } catch (ImportException e) {
                System.err.println("  ✗ Stage FAILED: " + stageName + " — " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.err.println("  ✗ Stage FAILED: " + stageName + " — " + e.getMessage());
                throw new ImportException(
                        "Unexpected error in stage [" + stageName + "]", e);
            }

            System.out.println("  ✓ Completed: " + stageName);
        }

        // Finalize statistics
        context.finalizeStatistics();

        System.out.println();
        System.out.println("  Pipeline complete. "
                + context.getStatistics().getSuccessCount() + " succeeded, "
                + context.getStatistics().getFailedCount() + " failed.");

        return new ImportReport<>(
                context.getStatistics(),
                context.getResults(),
                context.getExecutionLog()
        );
    }

    // ════════════════════════════════════════════════════════════════════
    // Builder
    // ════════════════════════════════════════════════════════════════════

    /**
     * Fluent builder for assembling an {@link ImportPipeline}.
     *
     * <p>Stages are added in order and executed in order.
     * Every method is optional — build only the pipeline you need.</p>
     *
     * @param <T> the domain type
     */
    public static class Builder<T> {

        private final List<Stage<T>> stages = new ArrayList<>();

        private Builder() {
        }

        /**
         * Add a reader stage (CSV, Excel, JSON, API, etc.).
         */
        public Builder<T> read(ReadStage<T> reader) {
            stages.add(reader);
            return this;
        }

        /**
         * Add a parser stage (raw records → domain objects).
         */
        public Builder<T> parse(ParseStage<T> parser) {
            stages.add(parser);
            return this;
        }

        /**
         * Add a validation stage.
         */
        public Builder<T> validate(ValidateStage<T> validator) {
            stages.add(validator);
            return this;
        }

        /**
         * Add a transformation / normalization stage.
         */
        public Builder<T> transform(TransformStage<T> transformer) {
            stages.add(transformer);
            return this;
        }

        /**
         * Add a deduplication stage.
         */
        public Builder<T> deduplicate(DeduplicateStage<T> deduplicator) {
            stages.add(deduplicator);
            return this;
        }

        /**
         * Add a custom processing stage.
         */
        public Builder<T> process(ProcessStage<T> processor) {
            stages.add(processor);
            return this;
        }

        /**
         * Add a storage stage that persists successful records to a repository.
         *
         * <p>This is a convenience method that creates an inline stage
         * wrapping the given {@link Repository}. Only records with
         * {@link RecordResult.Status#SUCCESS} (or non-failed pending) are stored.</p>
         */
        public Builder<T> store(Repository<T> repository) {
            stages.add(new StorageStage<>(repository));
            return this;
        }

        /**
         * Add a reporting stage.
         */
        public Builder<T> report(ReportStage<T> reporter) {
            stages.add(reporter);
            return this;
        }

        /**
         * Add a generic stage (for custom pipeline steps).
         */
        public Builder<T> addStage(Stage<T> stage) {
            stages.add(stage);
            return this;
        }

        /**
         * Build the immutable pipeline.
         */
        public ImportPipeline<T> build() {
            if (stages.isEmpty()) {
                throw new ImportException("Pipeline must have at least one stage");
            }
            return new ImportPipeline<>(stages);
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // Internal: Storage Stage adapter
    // ════════════════════════════════════════════════════════════════════

    /**
     * Internal stage that adapts a {@link Repository} into a {@link Stage}.
     * Stores only non-failed records, marks them as SUCCESS.
     */
    private static class StorageStage<T> implements ProcessStage<T> {

        private final Repository<T> repository;

        StorageStage(Repository<T> repository) {
            this.repository = repository;
        }

        @Override
        public void execute(ImportContext<T> context) {
            int stored = 0;

            for (RecordResult<T> result : context.getResults()) {
                if (!result.isFailed() && result.getParsedObject() != null) {
                    try {
                        repository.save(result.getParsedObject());
                        result.markSuccess();
                        stored++;
                    } catch (Exception e) {
                        result.addError("Storage error: " + e.getMessage());
                    }
                }
            }

            context.logStage(name(), "Stored " + stored + " records");
        }

        @Override
        public String name() {
            return "StorageStage[" + repository.getClass().getSimpleName() + "]";
        }
    }
}

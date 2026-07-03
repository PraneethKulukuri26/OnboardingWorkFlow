package com.importflow.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The shared mutable state that flows through every stage of the pipeline.
 *
 * <p>This is the <strong>heart</strong> of ImportFlow. Instead of passing
 * domain objects directly, every stage reads from and writes to the same
 * context. This decouples stages from each other and from the domain type.</p>
 *
 * <p>Lifecycle:</p>
 * <ol>
 *   <li>{@code Reader} populates {@link #rawRecords}</li>
 *   <li>{@code Parser} converts raw records → {@link #results} with parsed objects</li>
 *   <li>{@code Validator} / {@code Transformer} / {@code Deduplicator} update results</li>
 *   <li>{@code Repository} stores successful records</li>
 *   <li>{@code Reporter} reads results and statistics to produce output</li>
 * </ol>
 *
 * @param <T> the domain type (e.g., Employee)
 */
public class ImportContext<T> {

    // ── Raw input ──────────────────────────────────────────────────────
    private final List<RawRecord> rawRecords = new ArrayList<>();

    // ── Parsed & processed records ─────────────────────────────────────
    private final List<RecordResult<T>> results = new ArrayList<>();

    // ── Aggregate statistics ───────────────────────────────────────────
    private final ImportStatistics statistics = new ImportStatistics();

    // ── Configuration (e.g., file.path, delimiter) ─────────────────────
    private final Map<String, String> config = new HashMap<>();

    // ── Extensible metadata bag ────────────────────────────────────────
    private final Map<String, Object> metadata = new HashMap<>();

    // ── Stage execution log ────────────────────────────────────────────
    private final List<String> executionLog = new ArrayList<>();

    // ────────────────────────────────────────────────────────────────────
    // Configuration
    // ────────────────────────────────────────────────────────────────────

    /**
     * Initialize the context with configuration properties.
     */
    public ImportContext(Map<String, String> config) {
        if (config != null) {
            this.config.putAll(config);
        }
    }

    public String getConfig(String key) {
        return config.get(key);
    }

    public String getConfig(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getAllConfig() {
        return Map.copyOf(config);
    }

    // ────────────────────────────────────────────────────────────────────
    // Raw Records
    // ────────────────────────────────────────────────────────────────────

    public List<RawRecord> getRawRecords() {
        return rawRecords;
    }

    public void addRawRecord(RawRecord record) {
        rawRecords.add(record);
    }

    // ────────────────────────────────────────────────────────────────────
    // Record Results
    // ────────────────────────────────────────────────────────────────────

    public List<RecordResult<T>> getResults() {
        return results;
    }

    public void addResult(RecordResult<T> result) {
        results.add(result);
    }

    // ────────────────────────────────────────────────────────────────────
    // Statistics
    // ────────────────────────────────────────────────────────────────────

    public ImportStatistics getStatistics() {
        return statistics;
    }

    /**
     * Recompute statistics from current results.
     * Called automatically by the pipeline after all stages complete.
     */
    public void finalizeStatistics() {
        int total = results.size();
        int success = 0;
        int failed = 0;
        int duplicates = 0;

        for (RecordResult<T> r : results) {
            switch (r.getStatus()) {
                case SUCCESS  -> success++;
                case FAILED   -> failed++;
                case DUPLICATE -> { duplicates++; failed++; }
                default -> {}
            }
        }

        statistics.setTotalRecords(total);
        statistics.setSuccessCount(success);
        statistics.setFailedCount(failed);
        statistics.setDuplicateCount(duplicates);
    }

    // ────────────────────────────────────────────────────────────────────
    // Metadata
    // ────────────────────────────────────────────────────────────────────

    public void putMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getMetadata(String key) {
        return (V) metadata.get(key);
    }

    // ────────────────────────────────────────────────────────────────────
    // Execution Log
    // ────────────────────────────────────────────────────────────────────

    public void logStage(String stageName, String message) {
        executionLog.add("[" + stageName + "] " + message);
    }

    public List<String> getExecutionLog() {
        return List.copyOf(executionLog);
    }

    @Override
    public String toString() {
        return "ImportContext{rawRecords=" + rawRecords.size()
                + ", results=" + results.size()
                + ", statistics=" + statistics + "}";
    }
}

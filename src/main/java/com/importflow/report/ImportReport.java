package com.importflow.report;

import com.importflow.context.ImportStatistics;
import com.importflow.context.RecordResult;

import java.util.List;

/**
 * An immutable snapshot of the import results, suitable for
 * serialization, logging, or display.
 *
 * @param <T> the domain type
 */
public class ImportReport<T> {

    private final ImportStatistics statistics;
    private final List<RecordResult<T>> results;
    private final List<String> executionLog;

    public ImportReport(ImportStatistics statistics,
                        List<RecordResult<T>> results,
                        List<String> executionLog) {
        this.statistics = statistics;
        this.results = List.copyOf(results);
        this.executionLog = List.copyOf(executionLog);
    }

    public ImportStatistics getStatistics() {
        return statistics;
    }

    public List<RecordResult<T>> getResults() {
        return results;
    }

    public List<RecordResult<T>> getFailedResults() {
        return results.stream()
                .filter(RecordResult::isFailed)
                .toList();
    }

    public List<RecordResult<T>> getSuccessfulResults() {
        return results.stream()
                .filter(RecordResult::isSuccess)
                .toList();
    }

    public List<String> getExecutionLog() {
        return executionLog;
    }
}

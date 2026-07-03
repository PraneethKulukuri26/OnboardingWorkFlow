package com.importflow.context;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Aggregate statistics for the entire import run.
 *
 * <p>Thread-safe counters that stages increment as they process records.</p>
 */
public class ImportStatistics {

    private final AtomicInteger totalRecords = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger skippedCount = new AtomicInteger(0);
    private final AtomicInteger duplicateCount = new AtomicInteger(0);

    // ── Increment ──────────────────────────────────────────────────────

    public void incrementTotal() {
        totalRecords.incrementAndGet();
    }

    public void incrementSuccess() {
        successCount.incrementAndGet();
    }

    public void incrementFailed() {
        failedCount.incrementAndGet();
    }

    public void incrementSkipped() {
        skippedCount.incrementAndGet();
    }

    public void incrementDuplicate() {
        duplicateCount.incrementAndGet();
    }

    // ── Getters ────────────────────────────────────────────────────────

    public int getTotalRecords() {
        return totalRecords.get();
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getFailedCount() {
        return failedCount.get();
    }

    public int getSkippedCount() {
        return skippedCount.get();
    }

    public int getDuplicateCount() {
        return duplicateCount.get();
    }

    // ── Bulk set (for finalization) ────────────────────────────────────

    public void setTotalRecords(int total) {
        totalRecords.set(total);
    }

    public void setSuccessCount(int count) {
        successCount.set(count);
    }

    public void setFailedCount(int count) {
        failedCount.set(count);
    }

    public void setDuplicateCount(int count) {
        duplicateCount.set(count);
    }

    @Override
    public String toString() {
        return "ImportStatistics{total=" + totalRecords
                + ", success=" + successCount
                + ", failed=" + failedCount
                + ", skipped=" + skippedCount
                + ", duplicates=" + duplicateCount + "}";
    }
}

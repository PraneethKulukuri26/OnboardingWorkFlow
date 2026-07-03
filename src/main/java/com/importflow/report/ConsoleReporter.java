package com.importflow.report;

import com.importflow.context.ImportContext;
import com.importflow.context.ImportStatistics;
import com.importflow.context.RecordResult;
import com.importflow.stage.ReportStage;

import java.util.List;

/**
 * Built-in reporter that prints a formatted import summary to the console.
 *
 * <p>Produces a box-drawn report showing total/success/failed/duplicate
 * counts, followed by a detailed list of all failures with row numbers
 * and error messages.</p>
 *
 * @param <T> the domain type
 */
public class ConsoleReporter<T> implements ReportStage<T> {

    private static final String HORIZONTAL_LINE =
            "══════════════════════════════════════════════════════";
    private static final String THIN_LINE =
            "──────────────────────────────────────────────────────";

    @Override
    public void execute(ImportContext<T> context) {
        context.finalizeStatistics();
        ImportStatistics stats = context.getStatistics();
        List<RecordResult<T>> results = context.getResults();

        System.out.println();
        System.out.println("  ╔" + HORIZONTAL_LINE + "╗");
        System.out.println("  ║           I M P O R T   R E P O R T                ║");
        System.out.println("  ╠" + HORIZONTAL_LINE + "╣");
        System.out.printf("  ║  %-20s %6d                        ║%n",
                "Total Records:", stats.getTotalRecords());
        System.out.printf("  ║  %-20s %6d  ✓                     ║%n",
                "Successful:", stats.getSuccessCount());
        System.out.printf("  ║  %-20s %6d  ✗                     ║%n",
                "Failed:", stats.getFailedCount());
        System.out.printf("  ║  %-20s %6d  ⚡                    ║%n",
                "Duplicates:", stats.getDuplicateCount());
        System.out.println("  ╠" + HORIZONTAL_LINE + "╣");

        // ── Successful records ─────────────────────────────────────────
        List<RecordResult<T>> successes = results.stream()
                .filter(RecordResult::isSuccess)
                .toList();

        if (!successes.isEmpty()) {
            System.out.println("  ║  SUCCESSFUL RECORDS:                                ║");
            System.out.println("  ║  " + THIN_LINE.substring(0, 50) + "  ║");
            for (RecordResult<T> r : successes) {
                String line = String.format("  ║  ✓ Row %-4d  %s",
                        r.getRowNumber(),
                        truncate(String.valueOf(r.getParsedObject()), 38));
                System.out.printf("%-56s║%n", line);
            }
            System.out.println("  ╠" + HORIZONTAL_LINE + "╣");
        }

        // ── Failed records ─────────────────────────────────────────────
        List<RecordResult<T>> failures = results.stream()
                .filter(RecordResult::isFailed)
                .toList();

        if (!failures.isEmpty()) {
            System.out.println("  ║  FAILURES:                                          ║");
            System.out.println("  ║  " + THIN_LINE.substring(0, 50) + "  ║");
            for (RecordResult<T> r : failures) {
                for (String error : r.getErrors()) {
                    String line = String.format("  ║  ✗ Row %-4d  %s",
                            r.getRowNumber(), truncate(error, 38));
                    System.out.printf("%-56s║%n", line);
                }
            }
        } else {
            System.out.println("  ║  No failures — all records imported successfully!   ║");
        }

        System.out.println("  ╚" + HORIZONTAL_LINE + "╝");
        System.out.println();

        // ── Execution log ──────────────────────────────────────────────
        List<String> log = context.getExecutionLog();
        if (!log.isEmpty()) {
            System.out.println("  Pipeline Execution Log:");
            for (String entry : log) {
                System.out.println("    → " + entry);
            }
            System.out.println();
        }
    }

    /**
     * Truncate a string to maxLen, appending "..." if needed.
     */
    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen - 3) + "...";
    }

    @Override
    public String name() {
        return "ConsoleReporter";
    }
}

package com.importflow.report;

import com.importflow.context.ImportContext;
import com.importflow.context.RecordResult;
import com.importflow.stage.ReportStage;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A report stage that exports all failed records to a CSV file.
 *
 * <p>It re-uses the original headers from the input file, appends an
 * "Error Messages" column, and writes out every record that failed
 * or was marked as a duplicate.</p>
 *
 * <p>Configuration required: {@code error.file.path}</p>
 *
 * @param <T> the domain type
 */
public class CsvErrorExporter<T> implements ReportStage<T> {

    @Override
    public void execute(ImportContext<T> context) {
        String errorPath = context.getConfig("error.file.path");
        if (errorPath == null || errorPath.isBlank()) {
            context.logStage(name(), "Skipped: 'error.file.path' not configured.");
            return;
        }

        List<RecordResult<T>> failures = context.getResults().stream()
                .filter(r -> r.isFailed() || r.getStatus() == RecordResult.Status.DUPLICATE)
                .toList();

        if (failures.isEmpty()) {
            context.logStage(name(), "No failed records to export.");
            return;
        }

        File outFile = new File(errorPath);
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(outFile))) {
            
            // 1. Extract headers from the first failed record
            RecordResult<T> first = failures.get(0);
            Set<String> originalHeaders = first.getRawRecord().getFields().keySet();
            
            List<String> headerRow = new ArrayList<>(originalHeaders);
            headerRow.add("Error Messages"); // Append our new column
            
            writer.writeNext(headerRow.toArray(new String[0]));

            // 2. Write data rows
            for (RecordResult<T> result : failures) {
                Map<String, String> fields = result.getRawRecord().getFields();
                List<String> dataRow = new ArrayList<>();
                
                // Keep original columns in order
                for (String header : originalHeaders) {
                    dataRow.add(fields.getOrDefault(header, ""));
                }
                
                // Append concatenated errors
                String errorStr = String.join(" | ", result.getErrors());
                dataRow.add(errorStr);
                
                writer.writeNext(dataRow.toArray(new String[0]));
            }

            context.logStage(name(), "Exported " + failures.size() + " failed records to " + errorPath);

        } catch (IOException e) {
            context.logStage(name(), "Failed to write error CSV: " + e.getMessage());
            System.err.println("Failed to write error CSV to " + errorPath + ": " + e.getMessage());
        }
    }

    @Override
    public String name() {
        return "CsvErrorExporter";
    }
}

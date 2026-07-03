package com.importflow.reader;

import com.importflow.context.ImportContext;
import com.importflow.context.RawRecord;
import com.importflow.exception.StageException;
import com.importflow.stage.ReadStage;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in reader that parses a CSV file into {@link RawRecord}s.
 *
 * <p>Reads the file path from {@code context.getConfig("file.path")}.
 * The first row is treated as column headers. Each subsequent row becomes
 * a {@link RawRecord} with header→value mappings.</p>
 *
 * @param <T> the domain type (not used by reader, but required for pipeline typing)
 */
public class CsvReader<T> implements ReadStage<T> {

    @Override
    public void execute(ImportContext<T> context) {
        String filePath = context.getConfig("file.path");
        if (filePath == null || filePath.isBlank()) {
            throw new StageException(name(),
                    "Configuration 'file.path' is required but was not provided");
        }

        context.logStage(name(), "Reading CSV file: " + filePath);

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> allRows = csvReader.readAll();

            if (allRows.isEmpty()) {
                throw new StageException(name(), "CSV file is empty: " + filePath);
            }

            // First row = headers
            String[] headers = allRows.get(0);

            // Remaining rows = data
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                Map<String, String> fields = new LinkedHashMap<>();

                for (int col = 0; col < headers.length; col++) {
                    String value = (col < row.length) ? row[col].trim() : "";
                    fields.put(headers[col].trim(), value);
                }

                context.addRawRecord(new RawRecord(i, fields));
            }

            context.logStage(name(), "Read " + context.getRawRecords().size()
                    + " records from CSV");

        } catch (IOException e) {
            throw new StageException(name(),
                    "Failed to read CSV file: " + filePath, e);
        } catch (CsvException e) {
            throw new StageException(name(),
                    "Failed to parse CSV file: " + filePath, e);
        }
    }

    @Override
    public String name() {
        return "CsvReader";
    }
}

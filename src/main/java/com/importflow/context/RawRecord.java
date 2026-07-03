package com.importflow.context;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A single raw record read from the data source.
 *
 * <p>Wraps a {@code Map<String, String>} where keys are column headers
 * and values are the raw cell values (always strings at this stage).
 * Also carries the 1-based row number for error reporting.</p>
 */
public class RawRecord {

    private final int rowNumber;
    private final Map<String, String> fields;

    public RawRecord(int rowNumber, Map<String, String> fields) {
        this.rowNumber = rowNumber;
        this.fields = new LinkedHashMap<>(fields);
    }

    /**
     * 1-based row number in the source file (excluding the header).
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * Get a field value by column name (case-insensitive lookup).
     *
     * @param columnName the header name
     * @return the raw value, or {@code null} if not present
     */
    public String get(String columnName) {
        // Try exact match first, then case-insensitive
        String value = fields.get(columnName);
        if (value != null) {
            return value;
        }
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(columnName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * All fields as an unmodifiable map.
     */
    public Map<String, String> getFields() {
        return Map.copyOf(fields);
    }

    @Override
    public String toString() {
        return "RawRecord{row=" + rowNumber + ", fields=" + fields + "}";
    }
}

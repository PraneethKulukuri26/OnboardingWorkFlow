package com.importflow.exception;

/**
 * Thrown when a pipeline stage encounters an unrecoverable error.
 */
public class StageException extends ImportException {

    private final String stageName;

    public StageException(String stageName, String message) {
        super("[" + stageName + "] " + message);
        this.stageName = stageName;
    }

    public StageException(String stageName, String message, Throwable cause) {
        super("[" + stageName + "] " + message, cause);
        this.stageName = stageName;
    }

    public String getStageName() {
        return stageName;
    }
}

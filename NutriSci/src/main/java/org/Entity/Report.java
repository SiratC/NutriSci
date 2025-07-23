package org.Entity;

import java.time.LocalDateTime;

/**
 * An abstract class that saves the time of generated reports.
 */
public abstract class Report {
    /**
     * Timestap of report generated.
     */
    protected LocalDateTime generatedAt = LocalDateTime.now();

    /**
     * Returns timestamp of when the object is created.
     * @return time generated
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        return "Report created on: " + generatedAt;
    }
}

package org.Entity;

import java.time.LocalDateTime;

/**
 * An abstract class that saves the time of generated reports.
 */
public abstract class Report {
    /**
     * Timestamp of when the object is created.
     */
    protected LocalDateTime generatedAt = LocalDateTime.now();
}

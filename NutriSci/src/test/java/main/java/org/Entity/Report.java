package org.Entity;

import java.time.LocalDateTime;

public abstract class Report {
    protected LocalDateTime generatedAt = LocalDateTime.now();

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        return "Report created on: " + generatedAt;
    }
}

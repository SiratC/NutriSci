package org.Entity;

import java.time.LocalDate;

/**
 * Defines a time period for queries based on a start and end.
 */
public class DateRange {
    private LocalDate start;
    private LocalDate end;

    /**
     * Creates an instance for time period based on given start and end dates.
     *
     * @param start the start of the time period
     * @param end the end of the time period
     */
    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the start for the time query.
     *
     * @return start of time range
     */
    public LocalDate getStart() {
        return start;
    }

    /**
     * Returns the end for the time query.
     *
     * @return end of time range
     */
    public LocalDate getEnd() {
        return end;
    }
}

package org.Entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateRange {
    private LocalDate start;
    private LocalDate end;

    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public boolean contains(LocalDate date) {
        if (date == null || start == null || end == null) return false;
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public long getLengthInDays() {
        if (start == null || end == null) return 0;
        return ChronoUnit.DAYS.between(start, end) + 1;
    }
}

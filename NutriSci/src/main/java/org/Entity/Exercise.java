package org.Entity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

public class Exercise {
    private UUID id = UUID.randomUUID();
    private LocalDate date;
    private String type;
    private Duration duration;

    public Exercise(LocalDate date, String type, Duration duration) {
        this.date = date;
        this.type = type;
        this.duration = duration;
    }

    public LocalDate getDate(){
        return this.date;
    }
  
    public String getType(){
        return this.type;
    }

    public Duration getDuration(){
        return this.duration;
    }


  
}

package ru.dageev.schedule.payload.base;

import lombok.Value;

import java.time.Instant;

@Value
public class Schedule {
    Long id;
    String username;
    Instant startDate;
    Integer lengthInHours;
}

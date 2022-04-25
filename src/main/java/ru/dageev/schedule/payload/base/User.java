package ru.dageev.schedule.payload.base;

import lombok.Value;

@Value
public class User {
    String username;
    Integer totalTimeSchedule;
}

package ru.dageev.schedule.payload.request;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class AccumulatedUserScheduleRequest {
    @NotNull Long startTimeSeconds;
}

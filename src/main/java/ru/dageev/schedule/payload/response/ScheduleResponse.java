package ru.dageev.schedule.payload.response;

import lombok.Data;
import lombok.Value;
import ru.dageev.schedule.payload.base.Schedule;

import java.util.List;

@Value
public class ScheduleResponse {
    List<Schedule> schedules;
}

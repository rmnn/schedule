package ru.dageev.schedule.payload.response;

import lombok.Value;
import ru.dageev.schedule.payload.base.User;

import java.util.List;

@Value
public class AccumulatedUserScheduleResponse {
    List<User> users;
}

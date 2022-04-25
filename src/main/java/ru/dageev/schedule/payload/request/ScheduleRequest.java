package ru.dageev.schedule.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class ScheduleRequest {
    @NotBlank
    private String username;

    private Long startTimeSeconds;

    private Integer lengthInHours;

    public ScheduleRequest(String username) {
        this.username = username;
    }
}

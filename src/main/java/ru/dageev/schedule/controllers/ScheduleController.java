package ru.dageev.schedule.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dageev.schedule.models.User;
import ru.dageev.schedule.payload.base.Schedule;
import ru.dageev.schedule.payload.request.AccumulatedUserScheduleRequest;
import ru.dageev.schedule.payload.request.ScheduleRequest;
import ru.dageev.schedule.payload.response.AccumulatedUserScheduleResponse;
import ru.dageev.schedule.payload.response.MessageResponse;
import ru.dageev.schedule.payload.response.ScheduleResponse;
import ru.dageev.schedule.repository.ScheduleRepository;
import ru.dageev.schedule.repository.UserRepository;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import static java.sql.Timestamp.from;
import static java.time.Instant.ofEpochSecond;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedules")
@AllArgsConstructor
public class ScheduleController {
  private final UserRepository userRepository;
  private final ScheduleRepository scheduleRepository;


  @GetMapping
  @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
  public ResponseEntity<?> schedules(@Valid @RequestBody ScheduleRequest request) {
    User user = userRepository.findByUsername(request.getUsername()).orElse(null);
    if (user == null) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("Username not found"));
    }

    List<Schedule> schedules = getSchedules(request, user)
            .stream()
            .map(this::adapt)
            .toList();

    return ResponseEntity.ok(new ScheduleResponse(schedules));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> create(@Valid @RequestBody ScheduleRequest request) {
    User user = userRepository.findByUsername(request.getUsername()).orElse(null);
    if (user == null) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("Username not found"));
    }

    ru.dageev.schedule.models.Schedule schedule = new ru.dageev.schedule.models.Schedule(
            from(ofEpochSecond(request.getStartTimeSeconds())),
            request.getLengthInHours(),
            user
    );

    Schedule saved = adapt(scheduleRepository.save(schedule));
    return ResponseEntity.ok(new ScheduleResponse(List.of(saved)));
  }

  @DeleteMapping("/{scheduleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> delete(@PathVariable("scheduleId") Long scheduleId) {
    scheduleRepository.deleteById(scheduleId);
    return ResponseEntity.ok(new MessageResponse("Deleted"));
  }

  @GetMapping("/accumulates")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AccumulatedUserScheduleResponse> accumulatedUserSchedule(@Valid @RequestBody AccumulatedUserScheduleRequest request) {
    Timestamp timestamp = from(ofEpochSecond(request.getStartTimeSeconds()));
    List<ru.dageev.schedule.payload.base.User> users = userRepository.findByScheduleTimeAndOrder(timestamp)
            .stream()
            .map(user -> new ru.dageev.schedule.payload.base.User(
                            user.getUsername(),
                            user.getSchedules().stream()
                                    .map(ru.dageev.schedule.models.Schedule::getLengthInHours)
                                    .reduce(0, Integer::sum)
                    )
            )
            .toList();

    return ResponseEntity.ok(new AccumulatedUserScheduleResponse(users));
  }

  private Schedule adapt(ru.dageev.schedule.models.Schedule schedule) {
    return new Schedule(schedule.getId(),
            schedule.getUser().getUsername(),
            schedule.getStartDate().toInstant(),
            schedule.getLengthInHours()
    );
  }

  private Collection<ru.dageev.schedule.models.Schedule> getSchedules(ScheduleRequest request, User user) {
    return request.getStartTimeSeconds() == null
            ? user.getSchedules()
            : scheduleRepository.findAllAfterDate(user.getUsername(), from(ofEpochSecond(request.getStartTimeSeconds())));
  }
}

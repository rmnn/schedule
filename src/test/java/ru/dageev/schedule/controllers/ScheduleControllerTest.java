package ru.dageev.schedule.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import ru.dageev.schedule.models.User;
import ru.dageev.schedule.payload.base.Schedule;
import ru.dageev.schedule.payload.request.AccumulatedUserScheduleRequest;
import ru.dageev.schedule.payload.request.ScheduleRequest;
import ru.dageev.schedule.payload.response.AccumulatedUserScheduleResponse;
import ru.dageev.schedule.payload.response.ScheduleResponse;
import ru.dageev.schedule.repository.ScheduleRepository;
import ru.dageev.schedule.repository.UserRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ScheduleControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    ScheduleController scheduleController;

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    void testCRUDFlowShouldWork() {
        User user = userRepository.save(new User("user", "d@m.ru", "password"));

        ResponseEntity<?> response = scheduleController.create(new ScheduleRequest(user.getUsername(), now().getEpochSecond(), 100));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Schedule schedule = ((ScheduleResponse) response.getBody()).getSchedules().get(0);

        ScheduleResponse scheduleResponse = (ScheduleResponse)
                scheduleController.schedules(new ScheduleRequest(user.getUsername())).getBody();
        assertEquals(1, scheduleResponse.getSchedules().size());
        assertEquals(100, scheduleResponse.getSchedules().get(0).getLengthInHours());

        scheduleController.delete(schedule.getId());

        scheduleResponse = (ScheduleResponse)
                scheduleController.schedules(new ScheduleRequest(user.getUsername())).getBody();
        assertTrue(scheduleResponse.getSchedules().isEmpty());
    }

    @Test
    @WithMockUser(username = "user", roles = "STAFF")
    void testUserOrderByAccumulativeScheduleHoursShouldNotWorkForStaff() {
        assertThrows(RuntimeException.class,
                () -> scheduleController.accumulatedUserSchedule(new AccumulatedUserScheduleRequest(now().getEpochSecond())),
                "Expected authentication exception"
        );
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    void testUserOrderByAccumulativeScheduleHoursShouldNotWork() {
        User user1 = userRepository.save(new User("user1", "d@m1.ru", "password"));
        User user2 = userRepository.save(new User("user2", "d@m2.ru", "password"));

        ResponseEntity<?> response = scheduleController.create(new ScheduleRequest(user1.getUsername(), now().getEpochSecond(), 100));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = scheduleController.create(new ScheduleRequest(user2.getUsername(), now().getEpochSecond(), 1000));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = scheduleController.create(new ScheduleRequest(user2.getUsername(), now().getEpochSecond(), 500));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AccumulatedUserScheduleResponse ac = scheduleController.accumulatedUserSchedule(
                new AccumulatedUserScheduleRequest(now().minus(10, ChronoUnit.HOURS).getEpochSecond())
        ).getBody();

        List<ru.dageev.schedule.payload.base.User> users = List.of(
                new ru.dageev.schedule.payload.base.User("user2", 1500),
                new ru.dageev.schedule.payload.base.User("user1", 100)
        );

        assertEquals(users, ac.getUsers());
    }
}
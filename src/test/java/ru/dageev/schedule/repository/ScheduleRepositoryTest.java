package ru.dageev.schedule.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.dageev.schedule.models.Schedule;
import ru.dageev.schedule.models.User;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ScheduleRepositoryTest {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void testFindAllAfterDateShouldReturnCorrectList() {
        User user = userRepository.save(new User("user", "dasd@mail.ru", "password"));

        scheduleRepository.save(new Schedule(from(now()), 10, user));
        scheduleRepository.save(new Schedule(from(now().minus(20, ChronoUnit.DAYS)), 10, user));

        List<Schedule> all = scheduleRepository.findAllAfterDate("user", from(now().minus(21, ChronoUnit.DAYS)));
        assertEquals(2, all.size());

        List<Schedule> one = scheduleRepository.findAllAfterDate("user", from(now().minus(12, ChronoUnit.DAYS)));
        assertEquals(1, one.size());
    }
}
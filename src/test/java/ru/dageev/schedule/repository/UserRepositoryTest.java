package ru.dageev.schedule.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.dageev.schedule.models.Schedule;
import ru.dageev.schedule.models.User;

import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void testFindAllAfterDateShouldReturnCorrectList() {
        User user1 = userRepository.save(new User("user", "dasd@mail.ru", "password"));
        User user2 = userRepository.save(new User("user2", "dasd@mail2.ru", "password"));

        scheduleRepository.save(new Schedule(from(now()), 10, user1));
        scheduleRepository.save(new Schedule(from(now()), 100, user2));
        assertEquals(of(user2, user1), userRepository.findByScheduleTimeAndOrder(from(now().minus(5, HOURS))));

        scheduleRepository.save(new Schedule(from(now()), 1000, user1));
        assertEquals(of(user1, user2), userRepository.findByScheduleTimeAndOrder(from(now().minus(5, HOURS))));
    }

}
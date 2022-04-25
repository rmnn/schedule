package ru.dageev.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dageev.schedule.models.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  @Query("select u from User u join u.schedules s where s.startDate >= :startDate group by u order by sum(s.lengthInHours) desc")
  List<User> findByScheduleTimeAndOrder(Timestamp startDate);
}

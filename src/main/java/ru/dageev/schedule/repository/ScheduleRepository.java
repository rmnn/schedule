package ru.dageev.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dageev.schedule.models.Schedule;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s join s.user u where s.startDate >= :startDate and u.username = :username")
    List<Schedule> findAllAfterDate(@Param("username") String username, @Param("startDate") Timestamp creationDateTime);
}
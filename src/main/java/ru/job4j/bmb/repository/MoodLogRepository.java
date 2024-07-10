package ru.job4j.bmb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);

    /*
    Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);
     */

    @Query("SELECT m.user FROM MoodLog m WHERE m.createdAt >= ?1 AND m.createdAt <= ?2")
    List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay);

    /*
    List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart);
     */

    /*
    List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart);
     */
}

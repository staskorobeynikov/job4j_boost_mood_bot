package ru.job4j.bmb.service;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood mood = new Mood();
        mood.setId(moodId);
        moodLogRepository.save(new MoodLog(user, mood, Instant.now().getEpochSecond()));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        var content = new Content(chatId);
        Optional<User> user = userRepository.findByClientIdAndChatId(chatId, clientId);
        if (user.isPresent()) {
            long epochSecond = Instant.now().minus(Duration.ofDays(7)).getEpochSecond();
            List<MoodLog> moodLogs = moodLogRepository.findByUserId(user.get().getId())
                    .stream()
                    .filter(moodLog -> moodLog.getCreatedAt() >= epochSecond)
                    .toList();
            content.setText(formatMoodLogs(moodLogs, "Mood log for 1 week."));
        }
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        var content = new Content(chatId);
        Optional<User> user = userRepository.findByClientIdAndChatId(chatId, clientId);
        if (user.isPresent()) {
            long epochSecond = Instant.now().minus(Duration.ofDays(30)).getEpochSecond();
            List<MoodLog> moodLogs = moodLogRepository.findByUserId(user.get().getId())
                    .stream()
                    .filter(moodLog -> moodLog.getCreatedAt() >= epochSecond)
                    .toList();
            content.setText(formatMoodLogs(moodLogs, "Mood log for 1 month."));
        }
        return Optional.of(content);
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        var content = new Content(chatId);
        Optional<User> user = userRepository.findByClientIdAndChatId(chatId, clientId);
        if (user.isPresent()) {
            List<Achievement> achievements = achievementRepository.findByUserId(user.get().getId());
            content.setText(formatAwardsLogs(achievements));
        }
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    private String formatAwardsLogs(List<Achievement> achievements) {
        if (achievements.isEmpty()) {
            return "Awards : " + ": there are no awards.";
        }
        var sb = new StringBuilder("Awards : " + ":\n");
        achievements.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreateAt()));
            sb.append(formattedDate).append(": ").append(log.getAward().getTitle()).append("\n");
        });
        return sb.toString();
    }
}

package ru.job4j.bmb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

@Service
public class TgRemoteService extends TelegramLongPollingBot {

    private final String botName;

    private final String botToken;

    private final UserRepository userRepository;

    private final MoodContentRepository moodContentRepository;

    private final TgUI tgUI;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           UserRepository userRepository,
                           MoodContentRepository moodContentRepository, TgUI tgUI) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.moodContentRepository = moodContentRepository;
        this.tgUI = tgUI;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            moodContentRepository.findByMoodId(Long.valueOf(data)).ifPresent(
                    moodContent -> {
                        send(new SendMessage(String.valueOf(chatId), moodContent.getText()));
                    }
            );
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            long chatId = message.getChatId();
            long clientId = message.getFrom().getId();
            if ("/start".equals(message.getText())) {
                Optional<User> user = userRepository.findByClientIdAndChatId(clientId, chatId);
                if (user.isEmpty()) {
                    var newUser = new User();
                    newUser.setClientId(message.getFrom().getId());
                    newUser.setChatId(chatId);
                    userRepository.save(newUser);
                }
            }
            SendMessage buttons = new SendMessage();
            buttons.setChatId(chatId);
            buttons.setText("Как настроение сегодня?");
            buttons.setReplyMarkup(tgUI.buildButtons());
            send(buttons);
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

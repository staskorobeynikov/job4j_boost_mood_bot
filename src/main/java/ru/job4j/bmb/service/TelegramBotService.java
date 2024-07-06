package ru.job4j.bmb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.exception.SentContentException;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::send);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::send);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void send(Content content) {
        try {
            if (content.getAudio() != null) {
                var message = new SendAudio();
                message.setChatId(content.getChatId());
                message.setAudio(content.getAudio());
                if (content.getText() != null) {
                    message.setCaption(content.getText());
                }
                execute(message);
            } else if (content.getText() != null) {
                var message = new SendMessage();
                message.setChatId(content.getChatId());
                message.setText(content.getText());
                if (content.getMarkup() != null) {
                    message.setReplyMarkup(content.getMarkup());
                }
                execute(message);
            } else if (content.getPhoto() != null) {
                var message = new SendPhoto();
                message.setChatId(content.getChatId());
                message.setPhoto(content.getPhoto());
                if (content.getText() != null) {
                    message.setCaption(content.getText());
                }
                execute(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SentContentException(e.getMessage(), e);
        }
    }
}

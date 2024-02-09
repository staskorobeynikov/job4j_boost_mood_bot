package ru.job4j;

import ru.job4j.content.Content;
import ru.job4j.service.BotCommandHandler;
import ru.job4j.service.TelegramBotService;

public class DIByDirectInjectMain {
    public static void main(String[] args) {
        var handler = new BotCommandHandler();
        var tg = new TelegramBotService(handler);
        tg.receive(new Content());
    }
}

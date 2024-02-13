package ru.job4j.bmb;

import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.service.BotCommandHandler;
import ru.job4j.bmb.service.TelegramBotService;

public class DIByDirectInjectMain {
    public static void main(String[] args) {
        var handler = new BotCommandHandler();
        var tg = new TelegramBotService(handler);
        tg.receive(new Content());
    }
}

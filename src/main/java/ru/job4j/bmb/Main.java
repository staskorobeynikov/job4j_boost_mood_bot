package ru.job4j.bmb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.job4j.bmb.config.AppConfig;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.service.TelegramBotService;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner initTelegramApi(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramBotService.class);
            bot.receive(new Content());
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            AppConfig appConfig = ctx.getBean(AppConfig.class);
            appConfig.printConfig();
        };
    }

    @Bean
    public CommandLineRunner checkEnv(ApplicationContext ctx) {
        return args -> {
            System.out.println(ctx.getEnvironment().getProperty("telegram.bot.name"));
        };
    }
}
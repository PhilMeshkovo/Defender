package com.phil.antispam;

import com.phil.antispam.defender.AntiSpamBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class DefenderApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefenderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DefenderApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ApplicationContext ctx) {
        return args -> {
            AntiSpamBot antiSpamBot = ctx.getBean(AntiSpamBot.class);
            try {
                TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
                api.registerBot(antiSpamBot);
                LOGGER.info("Бот запущен");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }
}

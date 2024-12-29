package com.phil.antispam;

import com.phil.antispam.defender.AntiSpamBot;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class DefenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DefenderApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ApplicationContext ctx) {
        return args -> {
            // Получаем экземпляр бота и запускаем его
            AntiSpamBot antiSpamBot = ctx.getBean(AntiSpamBot.class);
            try {
                TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
                api.registerBot(antiSpamBot);
                System.out.println("Бот запущен!");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }
}

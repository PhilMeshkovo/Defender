package com.phil.antispam.config;

import com.phil.antispam.defender.AntiSpamBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    public AntiSpamBot antiSpamBot() {
        return new AntiSpamBot(botUsername, botToken);
    }
}

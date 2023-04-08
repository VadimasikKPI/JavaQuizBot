package com.example.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("bot.properties")
public class BotConfiguration {
    @Value("${bot.name}")
    String botName;

    @Value("${bot.key}")
    String botKey;

    public BotConfiguration(String botName, String botKey) {
        this.botName = botName;
        this.botKey = botKey;
    }

    public BotConfiguration() {

    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getBotKey() {
        return botKey;
    }

    public void setBotKey(String botKey) {
        this.botKey = botKey;
    }
}

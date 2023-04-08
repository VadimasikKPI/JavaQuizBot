package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    BotConfiguration configuration;



    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (message){
                case "/start":{
                    startCommand(chatId);
                break;}
                default:sendMessage(chatId, message);
            }
        }
    }

    public void startCommand(Long chatId){
        String response = "Hello, i am a simple bot, you can write me any message and i will return it to you)";
        sendMessage(chatId, response);
    }

    private void sendMessage(Long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("ERROR1");
        }
    }

    @Override
    public String getBotUsername() {
        return configuration.getBotName();
    }


    @Override
    public String getBotToken() {
        return configuration.getBotKey();
    }
}

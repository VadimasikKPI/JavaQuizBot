package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {


    BotConfiguration configuration;

    public TelegramBot(BotConfiguration configuration) {
        this.configuration = configuration;
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand("/start", "Initialize bot"));
        menu.add(new BotCommand("/quiz", "Start quiz"));

        try{
            this.execute(new SetMyCommands(menu, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e) {
            log.error("Error occurred in creating menu - " + e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (message){
                case "/start":{
                    startCommand(chatId);
                break;}
                case "/quiz":{
                    startQuiz(chatId);
                    break;
                }
                default:sendMessage(chatId, message);
            }
        }
    }

    private void startQuiz(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Question 1");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData("YES_BTN");

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("NO_BTN");

        row.add(yesButton);
        row.add(noButton);
        rows.add(row);

        markup.setKeyboard(rows);

        message.setReplyMarkup(markup);

        try{
            this.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred in sendMessage function - " + e);
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
        log.info("Send message to " + chatId + " text - " + text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred in sendMessage function - " + e);

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

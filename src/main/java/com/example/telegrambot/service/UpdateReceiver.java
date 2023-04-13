package com.example.telegrambot.service;

import com.example.telegrambot.handler.Handler;
import com.example.telegrambot.model.User;
import com.example.telegrambot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UpdateReceiver {

    private final List<Handler> handlers;

    private final UserRepository userRepository;

    public UpdateReceiver(List<Handler> handlers, UserRepository userRepository) {
        this.handlers = handlers;
        this.userRepository = userRepository;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update){
        try{
            if(isMessageWithText(update)){
                Message message = update.getMessage();
                int chatId = Math.toIntExact(message.getChatId());

                User user = userRepository.getByChatId(chatId).orElseGet(()-> userRepository.save(new User(chatId)));
                return getHandlerByState(user.getBotState()).handle(user,message.getText());
            }
            else if(update.hasCallbackQuery()){
                CallbackQuery callbackQuery = update.getCallbackQuery();
                int chatId = Math.toIntExact(callbackQuery.getFrom().getId());
                User user = userRepository.getByChatId(chatId).orElseGet(()->userRepository.save(new User(chatId)));

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user,callbackQuery.getData());
            }
            throw new UnsupportedOperationException();

        }
        catch (UnsupportedOperationException e){
            log.error("Error occurred in handle method - " + e);
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(State state){
        return handlers.stream().filter(h->h.operatedBotState()!=null)
                .filter(h->h.operatedBotState().equals(state))
                .findAny().orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String callbackQuery){
        return handlers.stream()
                .filter(h -> h.operatedCallBackQuery().stream().anyMatch(callbackQuery::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update){
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }


}

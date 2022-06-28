package com.work.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {


    public BotApiMethod<?> hadleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            System.out.println("!");
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return null;
        } else {
            System.out.println("!");
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            if (message.hasText()) {
                sendMessage.setText("Hello world");
                return sendMessage;
            }
        }
        return null;
    }
}

package ru.chernomoretc.telegrambot.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.chernomoretc.telegrambot.handler.CallbackQueryHandler;
import ru.chernomoretc.telegrambot.handler.MessageHandler;

import java.io.IOException;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {




    final CallbackQueryHandler callbackQueryHandler;
    final MessageHandler messageHandler;

//    @Value("${telegrambot.adminId}")
//    int adminId;


    public TelegramFacade( MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {
        this.callbackQueryHandler = callbackQueryHandler;

        this.messageHandler = messageHandler;
    }

    public BotApiMethod<?> handleUpdate(Update update) throws Exception {
        System.out.println(update.toString());
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null) {
                return messageHandler.answerMessage(update.getMessage());
            }
        }
        return null;
    }
    }




package ru.chernomoretc.telegrambot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.Role;
import ru.chernomoretc.telegrambot.services.ServiceUser;


import java.text.SimpleDateFormat;
import java.util.*;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {


    UserMessageHandler userMessageHandler;
    AdminMessageHandler adminMessageHandler;

    ServiceUser serviceUser;



    public BotApiMethod<?> answerMessage(Message message) throws Exception {
        Long chatId = message.getChatId();
        String fullName;
        if (message.getChat().getLastName() == null) {
            fullName = message.getChat().getFirstName().toString();
        } else {
            fullName = message.getChat().getFirstName().toString() + " " + message.getChat().getLastName().toString();
        }

        String inputText = message.getText();
        Date d = getDate(message.getDate());
        User user = serviceUser.getUser(fullName,chatId);
        if (user.getRole() == Role.SUPER_ADMIN || user.getRole() == Role.ADMIN) {
            return adminMessageHandler.handler(message, chatId, user, d, inputText);
        } else {
            return userMessageHandler.handler(message, chatId, user, d, inputText);
        }
    }


    private Date getDate(int sec) {
        Date date = new Date(sec * 1000L); // *1000 получаем миллисекунды
        return date;
    }

}

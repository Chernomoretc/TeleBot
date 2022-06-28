package ru.chernomoretc.telegrambot.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.chernomoretc.telegrambot.cash.BotStateCash;



@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {



    final BotStateCash botStateCash;

//    @Value("${telegrambot.adminId}")
//    int adminId;


    public TelegramFacade( BotStateCash botStateCash) {


        this.botStateCash = botStateCash;
    }

    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

        } else {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            if (message.hasText()) {
                sendMessage.setText(message.getText());
                return sendMessage;
            }
        }
        return null;
    }


}

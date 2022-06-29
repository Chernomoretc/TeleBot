package ru.chernomoretc.telegrambot.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.chernomoretc.telegrambot.cash.BotStateCash;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {

    Map<Integer, String> contact = new HashMap<>();

    final BotStateCash botStateCash;
    final ReplayKeyboardMaker replayKeyboardMaker;

//    @Value("${telegrambot.adminId}")
//    int adminId;


    public TelegramFacade(BotStateCash botStateCash, ReplayKeyboardMaker replayKeyboardMaker) {
        this.botStateCash = botStateCash;
        this.replayKeyboardMaker = replayKeyboardMaker;
    }

    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

        } else {
            Message message = update.getMessage();
            if (message.hasText() && message.hasEntities()) {
                contact.put(message.getChatId().intValue(), message.getContact().toString());
                Optional<MessageEntity> commandEntity =
                        message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
                if (commandEntity.isPresent()) {
                    String command =
                            message
                                    .getText()
                                    .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                    switch (command) {
                        case "/worked":
                            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), "Hello i am El-Service Bot");
                            getStartMessage(message.getChatId().toString());
                            break;
                    }
                }
            } else if (message.hasLocation()) {

                SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(
                                message.getLocation().toString() + " " + message.getContact().getFirstName().toString())
                        .build();
            } else if (message.hasText()) {
                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), message.getText());

            }
        }
        return null;
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Hello i am El-Service Bot");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replayKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }


}

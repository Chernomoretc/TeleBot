package ru.chernomoretc.telegrambot.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
@Component
public class ReplayKeyboardMaker {
    List<KeyboardRow> keyboard;

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardButton k1 = new KeyboardButton(ButtonNameEnum.START_OF_THE_WORKING_DAY.getButtonName());
        k1.setRequestLocation(true);
        KeyboardButton k2 = new KeyboardButton(ButtonNameEnum.END_OF_THE_WORKING_DAY.getButtonName());
        k2.setRequestLocation(true);



        KeyboardRow row1 = new KeyboardRow();
        row1.add(k1);
        row1.add(k2);

        keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);


        return replyKeyboardMarkup;
    }
}

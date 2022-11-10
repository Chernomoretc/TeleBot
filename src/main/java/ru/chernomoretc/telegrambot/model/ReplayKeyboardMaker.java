package ru.chernomoretc.telegrambot.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
@Component
public class ReplayKeyboardMaker {
    List<KeyboardRow> keyboardUser;
    List<KeyboardRow> keyboardAdmin;

    public ReplyKeyboardMarkup getMainMenuKeyboardAdmin()
    {
        KeyboardButton k1 = new KeyboardButton(ButtonNameEnum.ADD_OBJECT.getButtonName());
        KeyboardButton k2 = new KeyboardButton(ButtonNameEnum.DELETE_OBJECT.getButtonName());
        KeyboardButton k3 = new KeyboardButton(ButtonNameEnum.ALL_OBJECT.getButtonName());
        KeyboardButton k4 = new KeyboardButton(ButtonNameEnum.EMPLOYEE_REPORT.getButtonName());
        KeyboardButton k5 = new KeyboardButton(ButtonNameEnum.TRANSFER_ROLE.getButtonName());
        KeyboardButton k6 = new KeyboardButton(ButtonNameEnum.CREATE_ADMIN.getButtonName());
        KeyboardButton k7 = new KeyboardButton(ButtonNameEnum.CREATE_EXCEL.getButtonName());

        KeyboardRow row1 = new KeyboardRow();
        row1.add(k1);
        row1.add(k2);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(k3);
        row2.add(k5);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(k4);

        KeyboardRow row4 = new KeyboardRow();
        row4.add(k6);

        KeyboardRow row5= new KeyboardRow();
        row4.add(k7);

        keyboardAdmin = new ArrayList<>();
        keyboardAdmin.add(row1);
        keyboardAdmin.add(row2);
        keyboardAdmin.add(row3);
        keyboardAdmin.add(row4);
        keyboardAdmin.add(row5);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardAdmin);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;

    }
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardButton k1 = new KeyboardButton(ButtonNameEnum.START_OF_THE_WORKING_DAY.getButtonName());
        KeyboardButton k2 = new KeyboardButton(ButtonNameEnum.END_OF_THE_WORKING_DAY.getButtonName());
        KeyboardButton k3 = new KeyboardButton(ButtonNameEnum.SICK_LEAVE_DAY.getButtonName());
        KeyboardButton k4 = new KeyboardButton(ButtonNameEnum.GEO_LOCATION.getButtonName());
        KeyboardButton k5 = new KeyboardButton(ButtonNameEnum.VACATION.getButtonName());
        KeyboardButton k6 = new KeyboardButton(ButtonNameEnum.TIME_OFF.getButtonName());


        k4.setRequestLocation(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(k3);
        row1.add(k6);
        row1.add(k5);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(k1);
        row2.add(k2);
        row2.add(k4);


        keyboardUser = new ArrayList<>();
        keyboardUser.add(row1);
        keyboardUser.add(row2);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardUser);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
}

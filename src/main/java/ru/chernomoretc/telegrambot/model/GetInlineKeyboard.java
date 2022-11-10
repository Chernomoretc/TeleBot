package ru.chernomoretc.telegrambot.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.chernomoretc.telegrambot.entity.Object;
import ru.chernomoretc.telegrambot.services.ServiceObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class GetInlineKeyboard {

    ServiceObject serviceObject;

    public GetInlineKeyboard(ServiceObject serviceObject) {
        this.serviceObject = serviceObject;
    }

    /// true - текущий месяц, false - следующий
    public InlineKeyboardMarkup getDaysOfMonth(Boolean currentMonth, String data, Boolean setCallbackData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentDay;
        int allDay;
        if (currentMonth) {
            currentDay = cal.get(Calendar.DAY_OF_MONTH) + 1;
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            allDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            currentDay = 1;
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            cal.add(Calendar.MONTH, +1);
            allDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        for (; currentDay <= allDay; currentDay++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(String.valueOf(currentDay));

            if (setCallbackData) {
                inlineKeyboardButton.setCallbackData(data + "/" + currentDay + "/"+currentMonth);
            } else {
                inlineKeyboardButton.setSwitchInlineQueryCurrentChat(data+"/" + currentDay + "/"+currentMonth+"/Причина:");
            }

            if (keyboardButtonsRow.size() > 4) {

                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }

            keyboardButtonsRow.add(inlineKeyboardButton);
        }
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getButtonTimeOff() {
        return getDaysOfMonth(true, "TimeOFF",true);
    }




    public InlineKeyboardMarkup getObject() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        List<Object> list = serviceObject.findAll();
        if (list.isEmpty()) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Список обьектов пуст! Обратитесть к администратору группы");
            keyboardButtonsRow.add(inlineKeyboardButton);
        } else {
            for (Object o : list) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(String.valueOf(o.getObjectName()));
                inlineKeyboardButton.setCallbackData(String.valueOf("s/" + o.getObjectName()));

//                inlineKeyboardButton.setSwitchInlineQueryCurrentChat("s/"+o.getObjectName());
                if (keyboardButtonsRow.size() > 1) {

                    rowList.add(keyboardButtonsRow);
                    keyboardButtonsRow = new ArrayList<>();
                }

                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}




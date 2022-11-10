package ru.chernomoretc.telegrambot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.Role;
import ru.chernomoretc.telegrambot.model.GetInlineKeyboard;
import ru.chernomoretc.telegrambot.services.ServiceObject;
import ru.chernomoretc.telegrambot.services.ServiceShift;
import ru.chernomoretc.telegrambot.services.ServiceUser;
import ru.chernomoretc.telegrambot.utils.Utils;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {
    Utils sendMessageAdmin;

    GetInlineKeyboard getInlineKeyboard;

    ServiceUser serviceUser;
    ServiceShift serviceShift;

    ServiceObject serviceObject;

    UserMessageHandler userMessageHandler;
    Utils utils;

    @SneakyThrows
    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException {

        long adminId = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.SUPER_ADMIN).findFirst().get().getChatId();
        Long chatId = buttonQuery.getMessage().getChatId();
        String user;
        if (buttonQuery.getMessage().getChat().getLastName() == null) {
            user = buttonQuery.getMessage().getChat().getFirstName().toString();
        } else {
            user = buttonQuery.getMessage().getChat().getFirstName().toString() + " " + buttonQuery.getMessage().getChat().getLastName().toString();

        }
        String data = buttonQuery.getData();
        utils.deleteMessage(chatId.toString(), buttonQuery.getMessage().getMessageId().toString());


        if (data.startsWith("TimeOFF")) {
            String[] dataArray = data.split("/");
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[1]));
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Да");
            inlineKeyboardButton.setCallbackData("Y_TimeOFF/" + user + "/" + dataArray[1]);

            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Нет");
            inlineKeyboardButton1.setCallbackData("N_TimeOFF/" + user + "/" + dataArray[1]);

            keyboardButtonsRow.add(inlineKeyboardButton);
            keyboardButtonsRow.add(inlineKeyboardButton1);

            rowList.add(keyboardButtonsRow);

            inlineKeyboardMarkup.setKeyboard(rowList);


            SendMessage sendMessage = new SendMessage(String.valueOf(1034480700), String.format("Пользователь %s запрашивает отгул на %s ", user, calendarDate.getTime()));
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessageAdmin.sendMessage(chatId.toString(), String.format("Пользователь %s запрашивает отгул на %s", user, calendarDate.getTime()));

            return sendMessage;
        } else if (data.startsWith("Y_TimeOFF")) {
            String[] dataArray = data.split("/");
            User userService = serviceUser.findByName(dataArray[1]).get();

            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[2]));
            Date d = calendarDate.getTime();
            System.out.println(d);
            SendMessage sendMessage = new SendMessage(String.valueOf(adminId), String.format("Отгул подтвержден для пользователь %s на %s", userService.getFullName(), d));
            sendMessageAdmin.sendMessage(userService.getChatId().toString(), String.format("Отгул подтвержден руководством для пользователь %s на %s", userService.getFullName(), d));
            serviceShift.setTimeOff(userService, d);
            return sendMessage;
        } else if (data.startsWith("N_TimeOFF")) {
            String[] dataArray = data.split("/");
            User userService = serviceUser.findByName(dataArray[1]).get();

            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[2]));
            Date d = calendarDate.getTime();

            SendMessage sendMessage = new SendMessage(String.valueOf(adminId), String.format("Отгул не подтвержден для пользователь %s на %s", userService.getFullName(), d));

            sendMessageAdmin.sendMessage(userService.getChatId().toString(), String.format("Отгул не подтвержден руководством для пользователь %s на %s", userService.getFullName(), d));
            return sendMessage;
        } else if (data.startsWith("d")) {
            String[] dataArray = data.split("/");
            serviceObject.deleteObject(dataArray[1]);
            SendMessage sendMessage = new SendMessage(chatId.toString(), dataArray[1] + " обьект удален!");
            return sendMessage;
        } else if (data.startsWith("s")) {
            String[] dataArray = data.split("/");
            Date date = new Date();
            return userMessageHandler.handler(buttonQuery.getMessage(), chatId, serviceUser.getUser(user, chatId), date, data);
        } else if (data.startsWith("tr")) {
            String[] dataArray = data.split("/");
            Date date = new Date();
            serviceUser.setRole(Long.parseLong(dataArray[1]), Role.SUPER_ADMIN);
            serviceUser.setRole(Long.parseLong(dataArray[2]), Role.ADMIN);
            SendMessage sendMessage = new SendMessage(dataArray[1], "Вам переданна роль SuperAdmin!");
            utils.sendMessage(dataArray[2], String.format("Роль SuperAdmin успешно присвоена пользователю %s!", dataArray[3]));
            return sendMessage;
        } else if (data.startsWith("ca")) {
            String[] dataArray = data.split("/");
            Date date = new Date();
            serviceUser.setRole(Long.parseLong(dataArray[1]), Role.ADMIN);
            SendMessage sendMessage = new SendMessage(dataArray[1], "Вам присвоена роль Admin!");
            utils.sendMessage(dataArray[2], String.format("Роль admin успешно присвоена пользователю %s!", dataArray[3]));
            return sendMessage;
        } else if (data.startsWith("StartVacation")) {
            return setStartDayVacation(data);
        } else if (data.startsWith("StartDayVacation")) {
            return setEndVacation(data);
        } else if (data.startsWith("EndVacation")) {
            return setEndDayVacation(data);
        } else if (data.startsWith("EndDayVacation")) {
            System.out.println(data);
            return confirmVacation(data);
        } else if (data.startsWith("Y_Vacation/")) {
            String[] dataArray = data.split("/");
            Date start = new Date(dataArray[2]);
            Date end = new Date(dataArray[3]);
            SendMessage sendMessage = new SendMessage(dataArray[1], String.format("Отпуск подтвержден руководством с %s по %s", start, end));
            utils.sendMessage(String.valueOf(adminId), String.format("Отпуск подтвержден для пользователя %s с %s по %s", dataArray[4], start, end));
            return sendMessage;
        } else if (data.startsWith("N_Vacation/")) {
            String[] dataArray = data.split("/");
            SendMessage sendMessage = new SendMessage(dataArray[1], String.format("Отпуск не подтвержден руководством"));
            utils.sendMessage(String.valueOf(adminId), String.format("Отпуск не подтвержден для пользователя %s ", dataArray[2]));
            return sendMessage;
        }

        return null;
    }

    private BotApiMethod<?> confirmVacation(String data) throws IOException {
        String[] dataArray = data.split("/");
        String userFullName = dataArray[1];
        String chatId = dataArray[2];

        Calendar calendarStart = Calendar.getInstance();
        Date dateStart;
        Boolean startMonth = Boolean.parseBoolean(dataArray[4]);
        Calendar calendarEnd = Calendar.getInstance();
        Date dateEnd;
        Boolean endMonth = Boolean.parseBoolean(dataArray[6]);


        if (startMonth) {
            calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[3]));
            dateStart = calendarStart.getTime();
        } else {
            calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[3]));
            calendarStart.set(Calendar.MONTH, calendarStart.get(Calendar.MONTH) + 1);
            dateStart = calendarStart.getTime();
        }
        if (endMonth) {
            calendarEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[5]));
            dateEnd = calendarEnd.getTime();
        } else {
            calendarEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[5]));
            calendarEnd.set(Calendar.MONTH, calendarEnd.get(Calendar.MONTH) + 1);
            dateEnd = calendarEnd.getTime();
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Да");
        inlineKeyboardButton.setCallbackData("Y_Vacation/"+userFullName+"/" + chatId + "/" + dateStart.getTime() + "/" + dateEnd.getTime());

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Нет");
        inlineKeyboardButton1.setCallbackData("N_Vacation/" + chatId );

        keyboardButtonsRow.add(inlineKeyboardButton);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        System.out.println(chatId);
        SendMessage sendMessage = new SendMessage(chatId, String.format("Пользователь %s запрашивает отпуск с %s по %s"
                , userFullName, simpleDateFormat.format(dateStart), simpleDateFormat.format(dateEnd)));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessageAdmin.sendMessage(chatId, String.format("Заявка на подтверждения отпуска с %s по %s" +
                " отправленна руководству!", simpleDateFormat.format(dateStart), simpleDateFormat.format(dateEnd)));
        return sendMessage;
    }

    private BotApiMethod<?> setEndVacation(String data) {
        String[] dataArray = data.split("/");
        String userFullName = dataArray[1];
        String chatId = dataArray[2];

        Calendar calendarDate = Calendar.getInstance();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(new SimpleDateFormat("MMM").format(calendarDate.getTime()));
        inlineKeyboardButton.setCallbackData(String.format("EndVacation/%s/%s/%s/%s", userFullName, chatId, dataArray[3], dataArray[4]) + "/true");

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        calendarDate.add(Calendar.MONTH, +1);
        inlineKeyboardButton1.setText(new SimpleDateFormat("MMM").format(calendarDate.getTime()));
        inlineKeyboardButton1.setCallbackData(String.format("EndVacation/%s/%s/%s/%s", userFullName, chatId, dataArray[3], dataArray[4]) + "/false");

        keyboardButtonsRow.add(inlineKeyboardButton);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);


        SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите месяц окончания отпуска!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    private BotApiMethod<?> setStartDayVacation(String data) {
        System.out.println(data);
        String[] dataArray = data.split("/");
        String userFullName = dataArray[1];
        String chatId = dataArray[2];
        boolean currentMonth = Boolean.parseBoolean(dataArray[3]);
        SendMessage sendMessage = new SendMessage(chatId, "Выберите день начала отпуска!");
        sendMessage.setReplyMarkup(getInlineKeyboard.getDaysOfMonth(currentMonth,
                String.format("StartDayVacation/%s/%s", userFullName, chatId), true));
//        data + "/" + currentDay + "/"+currentMonth
        return sendMessage;
    }

    private BotApiMethod<?> setVacation(Long chatId, Message message, User user, Date d) {

        Calendar calendarDate = Calendar.getInstance();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(new SimpleDateFormat("MMM").format(calendarDate.getTime()));
        inlineKeyboardButton.setCallbackData("StartVacation/" + user.getFullName() + "/" + user.getChatId() + "/true");

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        calendarDate.add(Calendar.MONTH, +1);
        inlineKeyboardButton1.setText(new SimpleDateFormat("MMM").format(calendarDate.getTime()));
        inlineKeyboardButton1.setCallbackData("StartVacation/" + user.getFullName() + "/" + user.getChatId() + "/false");

        keyboardButtonsRow.add(inlineKeyboardButton);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);


        SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите месяц начала отпуска!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    private BotApiMethod<?> setEndDayVacation(String data) {
        String[] dataArray = data.split("/");
        String userFullName = dataArray[1];
        String chatId = dataArray[2];
        boolean currentMonthEnd = Boolean.parseBoolean(dataArray[5]);
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[3]));
        Date d = calendarDate.getTime();

        SendMessage sendMessage = new SendMessage(chatId, "Выберите день окончания отпуска!");
        sendMessage.setReplyMarkup(getInlineKeyboard.getDaysOfMonth(currentMonthEnd,
                String.format("EndDayVacation/%s/%s/%s/%s", userFullName, chatId, dataArray[3], dataArray[4]), true));
        return sendMessage;
    }
}

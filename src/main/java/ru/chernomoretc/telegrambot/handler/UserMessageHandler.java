package ru.chernomoretc.telegrambot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.chernomoretc.telegrambot.entity.Shift;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.AdminStatusShift;
import ru.chernomoretc.telegrambot.enumShift.Role;
import ru.chernomoretc.telegrambot.enumShift.StatusOpenShift;
import ru.chernomoretc.telegrambot.enumShift.StatusShift;
import ru.chernomoretc.telegrambot.model.ButtonNameEnum;
import ru.chernomoretc.telegrambot.model.GetInlineKeyboard;
import ru.chernomoretc.telegrambot.model.ReplayKeyboardMaker;
import ru.chernomoretc.telegrambot.services.ServiceLocationUser;
import ru.chernomoretc.telegrambot.services.ServiceObject;
import ru.chernomoretc.telegrambot.services.ServiceShift;
import ru.chernomoretc.telegrambot.services.ServiceUser;
import ru.chernomoretc.telegrambot.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserMessageHandler {
    Map<Long, StatusShift> shiftMap = new HashMap<>();
    Map<Long, String> objectMap = new HashMap<>();
    ReplayKeyboardMaker replyKeyboardMaker;
    GetInlineKeyboard getInlineKeyboard;
    ServiceShift serviceShift;
    ServiceLocationUser serviceLocationUser;

    ServiceObject serviceObject;

    Utils utils;

    ServiceUser serviceUser;



    public BotApiMethod<?> handler(Message message, Long chatId, User user, Date d, String inputText) throws Exception {
        System.out.println(chatId + "-" + user);
        if (shiftMap.get(chatId) == null) {
            shiftMap.put(chatId, StatusShift.DEFAULT);
        }
        if (message.hasLocation()) {
            return setLocation(chatId, message.getLocation(), user, d);
        } else if (inputText.startsWith("/worked")) {
            return setStartMessage(chatId);
        } else if (inputText.startsWith(ButtonNameEnum.START_OF_THE_WORKING_DAY.getButtonName())) {
            return setStartDay(chatId, user, d);
        } else if (inputText.startsWith(ButtonNameEnum.END_OF_THE_WORKING_DAY.getButtonName())) {
            return setEndDay(chatId, user, d);
        } else if (inputText.startsWith(ButtonNameEnum.SICK_LEAVE_DAY.getButtonName())) {
            return setSickLeaveDay(chatId, user, d);
        } else if (inputText.startsWith(ButtonNameEnum.VACATION.getButtonName())) {
            return setVacation(chatId, message, user, d);
        } else if (inputText.equals(ButtonNameEnum.TIME_OFF.getButtonName())) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите подходящий день в текущем месяце");
            sendMessage.setReplyMarkup(getInlineKeyboard.getButtonTimeOff());
            return sendMessage;
        }else if (inputText.startsWith("@el_robot_bot TimeOFF")) {
           return timeOFFRequest(chatId,user,inputText);
        }

        else if (inputText.startsWith("s/")) {
          return setObject(chatId,inputText);

        } else {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Нажмите подходящую кнопку");
            return sendMessage;
        }
    }


    private BotApiMethod<?> timeOFFRequest( Long chatId, User user, String inputText) throws IOException {
        long adminId = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.SUPER_ADMIN).findFirst().get().getChatId();
        String[] dataArray = inputText.split("/");
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[1]));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Да");
        inlineKeyboardButton.setCallbackData("Y_TimeOFF/" + user.getFullName() + "/" + dataArray[1]);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Нет");
        inlineKeyboardButton1.setCallbackData("N_TimeOFF/" + user.getFullName() + "/" + dataArray[1]);

        keyboardButtonsRow.add(inlineKeyboardButton);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);


        SendMessage sendMessage = new SendMessage(String.valueOf(1034480700), String.format("Пользователь %s запрашивает отгул на %s %s", user.getFullName(), calendarDate.getTime(),dataArray[2]));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        utils.sendMessage(chatId.toString(), String.format("Пользователь %s запрашивает отгул на %s %s", user.getFullName(), calendarDate.getTime(),dataArray[2]));

        return sendMessage;
    }

private BotApiMethod<?> setObject(Long chatId, String inputText)
{
    String[] dataArray = inputText.split("/");
    objectMap.put(chatId, dataArray[1]);
    Optional object = serviceObject.findByName(dataArray[1]);
    if (object.isPresent()) {
        shiftMap.put(chatId, StatusShift.OPEN);
        SendMessage sendMessage = new SendMessage(chatId.toString(), String.format("Вы выбрали обьект %s. Для открытия смены отправьте вашу геолокацию!", dataArray[1]));
        return sendMessage;

    } else {
        SendMessage sendMessage = new SendMessage(chatId.toString(), String.format("Обьект %s не существует.", dataArray[1]));
        return sendMessage;
    }
}
    private BotApiMethod<?> setVacation(Long chatId, Message message, User user, Date d) {

        Calendar calendarDate = Calendar.getInstance();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(new SimpleDateFormat("MMM").format(calendarDate.getTime()));
        inlineKeyboardButton.setCallbackData("StartVacation/" + user.getFullName() +"/"+user.getChatId()+"/true");

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        calendarDate.add(Calendar.MONTH,+1);
        inlineKeyboardButton1.setText(new SimpleDateFormat("MMM").format(calendarDate.getTime()));
        inlineKeyboardButton1.setCallbackData("StartVacation/" + user.getFullName()+"/"+user.getChatId() + "/false");

        keyboardButtonsRow.add(inlineKeyboardButton);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);


        SendMessage sendMessage = new SendMessage(chatId.toString(),"Выберите месяц начала отпуска!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;



    }

    private BotApiMethod<?> setLocation(Long chatId, Location l, User user, Date d) throws Exception {
        Long adminSuperId = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.SUPER_ADMIN).findFirst().get().getChatId();
        Long adminId = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.ADMIN).findFirst().get().getChatId();
        SendMessage sendMessage = new SendMessage(chatId.toString(), "error");
        if (shiftMap.get(chatId).equals(StatusShift.OPEN)) {
            serviceLocationUser.addLocation(l.getLatitude(), l.getLongitude(), d, user);
            shiftMap.put(chatId, StatusShift.DEFAULT);
            String comment = objectMap.get(chatId);
            Optional<Shift> optionalShift = serviceShift.getShiftByDateAndId(d, user.getId());
            if (optionalShift.isPresent()) {
                serviceShift.openShiftForAbsenteeism(d, user, comment);

            } else {
                serviceShift.openShift(d, user, comment);

            }
            sendMessage = new SendMessage(chatId.toString(),
                    String.format("Пользователь %s открыл смену %s " +
                            " ваша геолокация широта: %s, долгота: %s. Смена автоматически закроется в 18:10! ", user.getFullName(), d, l.getLatitude(), l.getLongitude()));
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
            utils.sendLocation(l.getLongitude().toString(), l.getLatitude().toString(), adminId.toString(), user.getFullName()+" открыл смену "+comment);
            utils.sendLocation(l.getLongitude().toString(), l.getLatitude().toString(), adminSuperId.toString(), user.getFullName()+" открыл смену "+comment);
            return sendMessage;

        } else if (shiftMap.get(chatId).equals(StatusShift.CLOSE)) {
            String comment = objectMap.get(chatId);
            serviceLocationUser.addLocation(l.getLatitude(), l.getLongitude(), d, user);
            shiftMap.put(chatId, StatusShift.DEFAULT);
            serviceShift.closeShift(d, user, false);
            sendMessage = new SendMessage(chatId.toString(),
                    String.format("Пользователь %s закрыл смену %s " +
                            "ваша геолокация широта: %s, долгота: %s  ", user.getFullName(), d, l.getLatitude(), l.getLongitude()));
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
            utils.sendLocation(l.getLongitude().toString(), l.getLatitude().toString(), adminId.toString(), user.getFullName()+" закрыл смену "+comment);
            utils.sendLocation(l.getLongitude().toString(), l.getLatitude().toString(), adminSuperId.toString(), user.getFullName()+" закрыл смену "+comment);
            return sendMessage;
        } else if (shiftMap.get(chatId).equals(StatusShift.DEFAULT)) {
            serviceLocationUser.addLocation(l.getLatitude(), l.getLongitude(), d, user);
            sendMessage = new SendMessage(chatId.toString(),
                    String.format("Пользователь %s %s " +
                            "ваша геолокация широта: %s, долгота: %s  ", user.getFullName(), d, l.getLatitude(), l.getLongitude()));
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
            utils.sendLocation(l.getLongitude().toString(), l.getLatitude().toString(), adminId.toString(), user.getFullName());
            utils.sendLocation(l.getLongitude().toString(), l.getLatitude().toString(), adminSuperId.toString(), user.getFullName());
            return sendMessage;
        }

        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }

    @SneakyThrows
    private BotApiMethod<?> setStartDay(Long chatId, User user, Date d) {
        System.out.println(user.getFullName());
        StatusOpenShift statusOpenShift = serviceShift.checkOpenShift(d, user);

        if (statusOpenShift == StatusOpenShift.SHIFT_OK) {
            //"Для открытия смены отправьте вашу геолокацию"
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Выбирите обьект!");
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(getInlineKeyboard.getObject());
//            shiftMap.put(chatId, StatusShift.OPEN);
            return sendMessage;
        } else {
            return SendMessage.builder().chatId(chatId.toString())
                    .text(statusOpenShift.getStatusName()).build();
        }
    }

    @SneakyThrows
    private BotApiMethod<?> setEndDay(Long chatId, User user, Date d) {
        StatusOpenShift statusOpenShift = serviceShift.checkCloseShift(d, user);

        if (statusOpenShift == StatusOpenShift.SHIFT_OK) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Для закрытия смены отправьте вашу геолокацию");
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
            shiftMap.put(chatId, StatusShift.CLOSE);
            return sendMessage;
        } else {
            return SendMessage.builder().chatId(chatId.toString())
                    .text(statusOpenShift.getStatusName()).build();
        }
    }

    private BotApiMethod<?> setSickLeaveDay(Long chatId, User user, Date d) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), String.format("Пользователь %s уже открыл смену %s", user, d));
        if (serviceShift.openSickLeave(d, user)) {
            sendMessage = new SendMessage(chatId.toString(), String.format("Пользователь %s на больничном %s", user.getFullName(), d));
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
            return sendMessage;
        }
        return sendMessage;
    }

    private SendMessage setStartMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), "Нажмите подходящую кнопку");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }

}

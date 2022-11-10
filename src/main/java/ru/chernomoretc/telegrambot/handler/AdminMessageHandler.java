package ru.chernomoretc.telegrambot.handler;


import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.*;

import org.apache.tomcat.util.net.AprEndpoint;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.chernomoretc.telegrambot.entity.Object;
import ru.chernomoretc.telegrambot.enumShift.AdminStatusShift;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.Role;
import ru.chernomoretc.telegrambot.enumShift.StatusOpenShift;
import ru.chernomoretc.telegrambot.model.ReplayKeyboardMaker;
import ru.chernomoretc.telegrambot.services.ServiceObject;
import ru.chernomoretc.telegrambot.services.ServiceShift;
import ru.chernomoretc.telegrambot.services.ServiceUser;
import ru.chernomoretc.telegrambot.utils.CreateExcel;
import ru.chernomoretc.telegrambot.utils.Utils;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EnableScheduling
@Data
@RequiredArgsConstructor
public class AdminMessageHandler {


    String botToken = "5550601179:AAHsNFCpjk314YWhNv0XPEsiGoBTzaWkSrA";
    ServiceUser serviceUser;
    ServiceShift serviceShift;
    ServiceObject serviceObject;
    ReplayKeyboardMaker replayKeyboardMaker;
    Utils utils;
    CreateExcel createExcel;



    @SneakyThrows
    public BotApiMethod<?> handler(Message message, Long chatId, User user, Date d, String inputText) {
        if (inputText.startsWith("/worked")) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Нажмите подходящую кнопку");
            sendMessage.setReplyMarkup(replayKeyboardMaker.getMainMenuKeyboardAdmin());
            return sendMessage;
        } else if (inputText.startsWith("Отчет по сотрудникам")) {
            sendReport(chatId);
            return null;

        } else if (inputText.equals("Добавить обьект")) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Ввидите название обьекта. Пример: add офис ");
            return sendMessage;

        } else if (inputText.equals("Удалить обьект")) {
            return deleteObject(chatId);

        } else if (inputText.equals("Показать все обьекты")) {
            return showAllObject(chatId);

        } else if (inputText.startsWith("add")) {
            return addObject(inputText, chatId);
        } else if (inputText.startsWith("Передать роль")) {
            return transferRole(chatId);


        } else if (inputText.startsWith("Создать админа")) {
            return createAdmin(chatId);


        } else if (inputText.startsWith("Cформировать отчет Excel")) {
            createExcel.createFile(8,2022,serviceShift.getShiftsByMonthAndYear(8,2022),serviceUser.findAll());
            File fin = new File("src//main//resources//Учет рабочего времени.xls");

//            byte [] buffer = new byte[fin.available()];
//            fin.read(buffer);
//            ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
            utils.uploadFile(chatId.toString(),fin);

//
//
            return null;
        } else {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Нажмите подходящую кнопку");
            sendMessage.setReplyMarkup(replayKeyboardMaker.getMainMenuKeyboardAdmin());
            return sendMessage;
        }


    }

    private BotApiMethod<?> createAdmin(Long chatId) {
        User superAdmin = serviceUser.findByChatId(chatId).get();
        if (superAdmin.getRole() == Role.SUPER_ADMIN) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            List<User> list = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.USER).collect(Collectors.toList());
            if (list.isEmpty()) {
                return new SendMessage(chatId.toString(), "Cписок пуст!");
            } else {
                SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите пользователя");
                for (User u : list) {
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText(String.valueOf(u.getFullName()));
                    inlineKeyboardButton.setCallbackData(String.valueOf("ca/" + u.getChatId() + "/" + superAdmin.getChatId() + "/" + u.getFullName()));

                    if (keyboardButtonsRow.size() > 1) {

                        rowList.add(keyboardButtonsRow);
                        keyboardButtonsRow = new ArrayList<>();
                    }

                    keyboardButtonsRow.add(inlineKeyboardButton);
                }
                rowList.add(keyboardButtonsRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                return sendMessage;
            }
        } else {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "У вас нет прав для данной функции!");
            return sendMessage;
        }

    }

    private BotApiMethod<?> transferRole(Long chatId) {

        User superAdmin = serviceUser.findByChatId(chatId).get();
        if (superAdmin.getRole() == Role.SUPER_ADMIN) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            List<User> list = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.ADMIN).collect(Collectors.toList());
            if (list.isEmpty()) {
                return new SendMessage(chatId.toString(), "Cписок пуст!");
            } else {
                SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите пользователя для передачи прав");
                for (User u : list) {
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText(String.valueOf(u.getFullName()));
                    inlineKeyboardButton.setCallbackData(String.valueOf("tr/" + u.getChatId() + "/" + superAdmin.getChatId() + "/" + u.getFullName()));

                    if (keyboardButtonsRow.size() > 1) {

                        rowList.add(keyboardButtonsRow);
                        keyboardButtonsRow = new ArrayList<>();
                    }

                    keyboardButtonsRow.add(inlineKeyboardButton);
                }
                rowList.add(keyboardButtonsRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                return sendMessage;
            }

        } else {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "У вас нет прав для данной функции!");
            return sendMessage;
        }
    }

    private BotApiMethod<?> showAllObject(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        List<Object> list = serviceObject.findAll();
        if (list.isEmpty()) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Список пуст!");
            return sendMessage;

        } else {
            for (Object o : list) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(String.valueOf(o.getObjectName()));
                inlineKeyboardButton.setCallbackData("all object");

                if (keyboardButtonsRow.size() > 1) {
                    rowList.add(keyboardButtonsRow);
                    keyboardButtonsRow = new ArrayList<>();
                }

                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);
            inlineKeyboardMarkup.setKeyboard(rowList);
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Список обьектов");
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            return sendMessage;
        }
    }

    private BotApiMethod<?> deleteObject(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        List<Object> list = serviceObject.findAll();
        if (list.isEmpty()) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Список пуст!");
            return sendMessage;

        } else {
            for (Object o : list) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(String.valueOf(o.getObjectName()));
                inlineKeyboardButton.setCallbackData(String.valueOf("d/" + o.getObjectName()));

                if (keyboardButtonsRow.size() > 1) {

                    rowList.add(keyboardButtonsRow);
                    keyboardButtonsRow = new ArrayList<>();
                }

                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);
            inlineKeyboardMarkup.setKeyboard(rowList);
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите обьект для удаления");
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            return sendMessage;
        }


    }

    @Scheduled(cron = "0 0 10 * * MON-FRI", zone = "Asia/Irkutsk")
    public void reportCurrentTime() throws IOException {
        List<User> userList = serviceUser.findAll();
        long superAdmin = userList.stream().filter(u -> u.getRole() == Role.SUPER_ADMIN).findFirst().get().getChatId();
        long admin = userList.stream().filter(u -> u.getRole() == Role.ADMIN).findFirst().get().getChatId();
        sendReport(superAdmin);
        sendReport(admin);

    }

    @Scheduled(cron = "0 10 18 * * MON-FRI", zone = "Asia/Irkutsk")
    public void closeShiftAuto() throws IOException {
        List<User> userList = serviceUser.findAll();
        StringBuilder statusUsers = new StringBuilder();
        Date d = new Date();
        for (User u : userList) {
            StatusOpenShift statusOpenShift = serviceShift.checkCloseShift(d, u);
            if (statusOpenShift == StatusOpenShift.SHIFT_OK) {
                serviceShift.closeShift(d, u, false);
                sendMessage(u.getChatId(),
                        String.format("Смена закрыта автоматически для пользователя %s  %s"
                                , u.getFullName(), d));
            }
        }
    }

    public void sendMessage(Long chatId, String text) throws IOException {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?text=%s&chat_id=%s", botToken, text, chatId.toString());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
    }

    public void sendReport(Long chatId) throws IOException {
        List<User> userList = serviceUser.findAll().stream().filter(u -> u.getRole() == Role.USER).collect(Collectors.toList());
        StringBuilder statusUsers = new StringBuilder();
        Date d = new Date();
        statusUsers.append(d);
        statusUsers.append("%0A");
        for (User u : userList) {

            AdminStatusShift adminStatusShift = serviceShift.checkShiftAdmin(d, u);
            switch (adminStatusShift) {

                case VACATION:
                    statusUsers.append(String.format("%s - %s ", u.getFullName(), "отпуск"));
                    break;
                case ABSENTEEISM:
                    statusUsers.append(String.format("%s - %s ", u.getFullName(), "прогул"));
                    break;
                case SICK_LEAVE:
                    statusUsers.append(String.format("%s - %s ", u.getFullName(), "больничный"));
                    break;
                case BEING_LATE:
                    statusUsers.append(String.format("%s - %s ", u.getFullName(), "опаздание"));
                    break;
                case TIME_OFF:
                    statusUsers.append(String.format("%s - %s ", u.getFullName(), "отгул"));
                    break;
                case OK:
                    statusUsers.append(String.format("%s - %s  ", u.getFullName(), "все OK"));
                    break;
                case ABSENTEEISM_CREATE:
                    statusUsers.append(String.format("%s - %s ", u.getFullName(), "прогул"));
                    serviceShift.setAbsenteeism(u, d);
                    break;
            }
            statusUsers.append("%0A");
        }
        sendMessage(chatId, statusUsers.toString());
    }

    public BotApiMethod<?> addObject(String inputText, Long chatId) {
        String[] arr = inputText.split(" ");
        StringBuilder objectName = new StringBuilder();
        for (int i = 1; i < arr.length; i++) {
            objectName.append(arr[i] + " ");
        }
        if (serviceObject.findByName(objectName.toString()).isPresent()) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Такой обьект уже существует");
            return sendMessage;
        } else {
            if (arr.length > 2) {

                serviceObject.addObject(objectName.toString());
                SendMessage sendMessage = new SendMessage(chatId.toString(), objectName.toString() + " обьект добавлен!");
                return sendMessage;

            } else {
                SendMessage sendMessage = new SendMessage(chatId.toString(), "Некорректные данные, попробуйте еще раз! Пример: add офис ");
                return sendMessage;
            }

        }
    }
}

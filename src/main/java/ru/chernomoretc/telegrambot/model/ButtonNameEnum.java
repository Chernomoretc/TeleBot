package ru.chernomoretc.telegrambot.model;

import org.springframework.stereotype.Component;

public enum ButtonNameEnum {
    START_OF_THE_WORKING_DAY("Начало дня \ud83c\udf1e" ),
    END_OF_THE_WORKING_DAY("Конец дня \ud83c\udf19"),
    SICK_LEAVE_DAY("Больничный \u271a"),
    GEO_LOCATION("Геолокация \ud83c\udf0d"),
    VACATION("Отпуск \ud83c\udf34"),

    ADD_OBJECT("Добавить обьект"),

    DELETE_OBJECT("Удалить обьект"),

    ALL_OBJECT("Показать все обьекты"),

    EMPLOYEE_REPORT("Отчет по сотрудникам"),

    TRANSFER_ROLE("Передать роль"),

    CREATE_ADMIN("Создать админа"),

    CREATE_EXCEL("Cформировать отчет Excel"),
    TIME_OFF("Отгул");
    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }
}

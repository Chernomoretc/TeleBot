package ru.chernomoretc.telegrambot.model;

import org.springframework.stereotype.Component;

public enum ButtonNameEnum {
    START_OF_THE_WORKING_DAY("Начало дня"),
    END_OF_THE_WORKING_DAY("Конец дня");
    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }
}

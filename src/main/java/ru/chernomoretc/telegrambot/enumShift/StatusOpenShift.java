package ru.chernomoretc.telegrambot.enumShift;

public enum StatusOpenShift {
    SHIFT_IS_OPEN("Смена уже открыта!"),
    SHIFT_IS_NOT_OPEN("Смена еще не открыта!"),
    SHIFT_IS_CLOSE("Смена уже закрыта! "),
    SHIFT_IS_OPEN_VACATION("Смена уже открыта!(Отпуск)"),
    SHIFT_IS_OPEN_SICK_LEAVE("Смена уже открыта!(Больничный)"),
    SHIFT_OK("shift_ok"),
    SHIFT_ABSENTEEISM("ABSENTEEISM");

    private final String statusName;


    StatusOpenShift(String statusName) { this.statusName = statusName;
    }
    public String getStatusName() {return statusName;}
}

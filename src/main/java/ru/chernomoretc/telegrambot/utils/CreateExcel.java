package ru.chernomoretc.telegrambot.utils;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Component;
import ru.chernomoretc.telegrambot.entity.Shift;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.AdminStatusShift;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class CreateExcel {
    String pattern = "MMM yyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String patternForCell = "d-M-yyy";
    SimpleDateFormat dateFormatForCell = new SimpleDateFormat(patternForCell);
    String patternHM = "kk : mm";
    SimpleDateFormat dateFormatHM = new SimpleDateFormat(patternHM);
    Utils utils;


    public void createFile(int month, int year, List<Shift> shifts, List<User> users) throws IOException {
        String path = "src/main/resources";
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        Date firstDay = cal.getTime();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        int allDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date endDay = cal.getTime();

        int endCellNum;
        File file = new File(path + File.separator + "Учет рабочего времени.xls");
       // file.mkdirs();
        file.createNewFile();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("подробный отчет " + Month.of((month + 1)).toString());
        HSSFSheet sheet1 = workbook.createSheet("краткий отчет " + Month.of((month + 1)).toString());
        HSSFCellStyle styleH = headStyle(workbook);
        HSSFCellStyle styleC = cellsStyle(workbook);
        //Color style
        //vacation
        HSSFCellStyle sVacation = cellsStyle(workbook);
        sVacation.setFillForegroundColor(HSSFColor.GREEN.index);
        sVacation.setFillPattern(HSSFCellStyle.ALT_BARS);
        //sick leave
        HSSFCellStyle sSickLeave = cellsStyle(workbook);
        sSickLeave.setFillForegroundColor(HSSFColor.YELLOW.index);
        sSickLeave.setFillPattern(HSSFCellStyle.ALT_BARS);
        //being_late
        HSSFCellStyle sBeingLate = cellsStyle(workbook);
        sBeingLate.setFillForegroundColor(HSSFColor.BLUE_GREY.index);
        sBeingLate.setFillPattern(HSSFCellStyle.ALT_BARS);
        //OK
        HSSFCellStyle sOk = cellsStyle(workbook);
        sOk.setFillForegroundColor(HSSFColor.BLUE.index);
        sOk.setFillPattern(HSSFCellStyle.ALT_BARS);
        //OK
        HSSFCellStyle sAbsenteeism = cellsStyle(workbook);
        sAbsenteeism.setFillForegroundColor(HSSFColor.RED.index);
        sAbsenteeism.setFillPattern(HSSFCellStyle.ALT_BARS);

        int rowNum = 0;
        int cellNum = 0;
        int rowNumMin = 0;
        int cellNumMin = 0;
        HSSFRow row = sheet.createRow(rowNum);
        HSSFRow rowMin = sheet1.createRow(rowNumMin);
        HSSFCell cell;
        HSSFCell cellMin;

///подробный отчет
        cell = row.createCell(0);
        cell.setCellValue("Учет рабочего времени");
        cell.setCellStyle(headStyle(workbook));

        cell = row.createCell(2);
        cell.setCellValue("Отпуск");
        cell.setCellStyle(sVacation);

        cell = row.createCell(3);
        cell.setCellValue("Больничный");
        cell.setCellStyle(sSickLeave);

        cell = row.createCell(4);
        cell.setCellValue("Прогул");
        cell.setCellStyle(sAbsenteeism);

        cell = row.createCell(5);
        cell.setCellValue("Опаздание");
        cell.setCellStyle(sBeingLate);

        cell = row.createCell(6);
        cell.setCellValue("Ок");
        cell.setCellStyle(sOk);
        rowNum++;

        row = sheet.createRow(rowNum);

        cell = row.createCell(cellNum);
        cell.setCellValue("Дата");
        cell.setCellStyle(styleH);

        ///краткий отчет
        cellMin = rowMin.createCell(0);
        cellMin.setCellValue("фио");
        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(1);
        cellMin.setCellValue("отпуск\nдни");
        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(2);
        cellMin.setCellValue("больничный\nдни");
        cellMin.setCellStyle(styleH);

//        cellMin = rowMin.createCell(3);
//        cellMin.setCellValue("больничный\nчасы");
//        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(3);
        cellMin.setCellValue("прогул\nдни");
        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(4);
        cellMin.setCellValue("опаздание\nдни");
        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(5);
        cellMin.setCellValue("отработанно\nдней");
        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(6);
        cellMin.setCellValue("отработанно\nчасов");
        cellMin.setCellStyle(styleH);

        cellMin = rowMin.createCell(7);
        cellMin.setCellValue("итого\nчасов");
        cellMin.setCellStyle(styleH);
        rowNumMin++;

        for (int i = 1; i <= allDay; i++) {
            rowNum++;
            row = sheet.createRow(rowNum);
            cell = row.createCell(cellNum);
            cal.set(year, month, i);
            cell.setCellValue(dateFormatForCell.format(cal.getTime()));
            cell.setCellStyle(styleC);
        }

        for (User user : users) {
            rowMin = sheet1.createRow(rowNumMin);
            int sumVacationDay = 0;
            int sumSickLeaveDay = 0;
            double sumSickLeaveHours = 0.0;
            int sumAbsenteeism = 0;
            int sumWorkedDay = 0;
            int sumBeingLate = 0;
            double sumAllHours = 0.0;
            double sumHours = 0.0;

            rowNum = 1;
            row.setRowNum(rowNum);
            cellNum++;
            cell = row.createCell(cellNum);
            cell.setCellValue("\n" + user.getFullName());
            cell.setCellStyle(styleH);
            cellNum++;
            cell = row.createCell(cellNum);
            cell.setCellValue("Время \nприбытия \nна объект");
            cell.setCellStyle(styleH);
            cellNum++;
            cell = row.createCell(cellNum);
            cell.setCellValue("Время\nубытия\nс объекта");
            cell.setCellStyle(styleH);
            cellNum++;
            cell = row.createCell(cellNum);
            cell.setCellValue("Отработанное\nвремя");
            cell.setCellStyle(styleH);
            rowNum++;
            cellNum = cellNum - 3;
            for (int i = 1; i <= allDay; i++) {

                row.setRowNum(rowNum);
                Optional<Shift> shift = findShiftByUserAndDate(user, i, shifts);
                if (shift.isPresent()) {
                    if (shift.get().getStatus() == AdminStatusShift.VACATION) {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(sVacation);
                        cellNum++;
                        sumVacationDay++;

                    } else if (shift.get().getStatus() == AdminStatusShift.ABSENTEEISM) {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(sAbsenteeism);
                        cellNum++;
                        sumAbsenteeism++;

                    } else if (shift.get().getStatus() == AdminStatusShift.BEING_LATE) {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(sBeingLate);
                        cellNum++;
                        sumBeingLate++;

                    } else if (shift.get().getStatus() == AdminStatusShift.SICK_LEAVE) {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(sSickLeave);
                        cellNum++;
                        sumSickLeaveDay++;

                    } else if (shift.get().getStatus() == AdminStatusShift.OK) {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(sOk);
                        cellNum++;
                        sumWorkedDay++;

                    } else {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(styleC);
                        cellNum++;
                    }
                    ;
                    if (shift.get().getClose() != null) {

                        cell = row.createCell(cellNum);
                        cell.setCellValue(dateFormatHM.format(shift.get().getOpen()));
                        cell.setCellStyle(styleC);
                        cellNum++;
                        cell = row.createCell(cellNum);
                        cell.setCellValue(dateFormatHM.format(shift.get().getClose()));
                        cell.setCellStyle(styleC);
                        cellNum++;
                        cell = row.createCell(cellNum);
                        sumHours = sumHours + shift.get().getWorkHours();
                        cell.setCellValue(shift.get().getWorkHours());
                        cell.setCellStyle(styleC);
                        cellNum = cellNum - 3;
                    } else {
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(styleC);
                        cellNum++;
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(styleC);
                        cellNum++;
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(styleC);
                        cellNum++;
                        cell = row.createCell(cellNum);
                        cell.setCellStyle(styleC);
                        cellNum = cellNum - 4;
                    }
                } else {

                    cell = row.createCell(cellNum);
                    cell.setCellStyle(styleC);
                    cellNum++;
                    cell = row.createCell(cellNum);
                    cell.setCellStyle(styleC);
                    cellNum++;
                    cell = row.createCell(cellNum);
                    cell.setCellStyle(styleC);
                    cellNum++;
                    cell = row.createCell(cellNum);
                    cell.setCellStyle(styleC);
                    cellNum = cellNum - 3;

                }
                rowNum++;
            }
            cellNum++;
            cellNum++;
            cellNum++;

            cellMin = rowMin.createCell(0);
            cellMin.setCellValue(user.getFullName());
            cellMin.setCellStyle(styleH);

            cellMin = rowMin.createCell(1);
            cellMin.setCellValue(sumVacationDay);
            cellMin.setCellStyle(styleH);

            cellMin = rowMin.createCell(2);
            cellMin.setCellValue(sumSickLeaveDay);
            cellMin.setCellStyle(styleH);


            sumSickLeaveHours = sumSickLeaveDay * 9;


            cellMin = rowMin.createCell(3);
            cellMin.setCellValue(sumAbsenteeism);
            cellMin.setCellStyle(styleH);

            cellMin = rowMin.createCell(4);
            cellMin.setCellValue(sumBeingLate);
            cellMin.setCellStyle(styleH);

            cellMin = rowMin.createCell(5);
            cellMin.setCellValue(sumWorkedDay);
            cellMin.setCellStyle(styleH);

            cellMin = rowMin.createCell(6);
            cellMin.setCellValue(sumHours);
            cellMin.setCellStyle(styleH);

            cellMin = rowMin.createCell(7);
            sumAllHours = sumSickLeaveHours + sumHours;
            cellMin.setCellValue(sumAllHours);
            cellMin.setCellStyle(styleH);
            rowNumMin++;

        }
        for (int i = 0;i<=150;i++)
        {sheet.autoSizeColumn(i);
        }
        for (int i = 0;i<=8;i++)
        {sheet1.autoSizeColumn(i);}

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
        outFile.flush();
        outFile.close();
        workbook.close();
    }

    public double findShiftByDay(User user, int day, List<Shift> shifts) {
        Optional<Shift> shift = shifts.stream().filter(s ->
                s.getUser().getFullName().equals(user.getFullName())
        ).filter(s -> Instant.ofEpochMilli(s.getCreated().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate().getDayOfMonth() == day).findFirst();
        if (shift.isPresent()) {
            System.out.println(shift.get());
            return shift.get().getWorkHours();
        } else {
            return 0;
        }

    }

    public HSSFCellStyle headStyle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle style = workbook.createCellStyle();
        // и применяем к этому стилю жирный шрифт
        style.setFont(font);
        // Определение граничных значений стиля
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        // Определение цвета граничных значений стиля
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setWrapText(true);

        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        return style;
    }

    public HSSFCellStyle cellsStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        // и применяем к этому стилю жирный шрифт
        // Определение граничных значений стиля
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        // Определение цвета граничных значений стиля
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setWrapText(true);

        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        return style;
    }

    public Optional<Shift> findShiftByUserAndDate(User user, int day, List<Shift> shifts) {
        return shifts.stream().filter(s ->
                s.getUser().getFullName().equals(user.getFullName())
        ).filter(s -> Instant.ofEpochMilli(s.getCreated().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate().getDayOfMonth() == day).findFirst();

    }
}

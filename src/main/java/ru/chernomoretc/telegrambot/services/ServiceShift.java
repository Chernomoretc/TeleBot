package ru.chernomoretc.telegrambot.services;

import org.springframework.stereotype.Service;
import ru.chernomoretc.telegrambot.enumShift.AdminStatusShift;
import ru.chernomoretc.telegrambot.entity.Shift;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.StatusOpenShift;
import ru.chernomoretc.telegrambot.repositories.ShiftRepo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service

public class ServiceShift {
    ShiftRepo shiftRepo;

    public ServiceShift(ShiftRepo shiftRepo) {
        this.shiftRepo = shiftRepo;
    }


    public List<Shift> getAllShifts() {
        return shiftRepo.findAll();
    }


    public void openShift(Date openDate, User user,String comment) throws Exception {
        Shift shift = new Shift();
        shift.setOpen(openDate);
        shift.setUser(user);
        shift.setComment(comment);
        shiftRepo.save(shift);
    }

    public boolean openSickLeave(Date date, User user) {
        Optional<Shift> optionalShift = shiftRepo.currentShift(date, user.getId());
        if (optionalShift.isPresent()) {
            ///проверка на существование смены
            return false;
        } else {
            Shift shift = new Shift();
            shift.setUser(user);
            shift.setWorkHours(9);
            shift.setStatus(AdminStatusShift.SICK_LEAVE);
            shiftRepo.save(shift);
            return true;
        }

    }


    public void setComment(String comment, Date date, Long id) {
        Shift shift = shiftRepo.currentShift(date, id).get();
        String lastComment = shift.getComment();
        StringBuilder stringBuilder = new StringBuilder();
        if (lastComment.isEmpty()) {
            stringBuilder.append(comment);
        } else {
            stringBuilder.append("," + comment);
        }
        shift.setComment(stringBuilder.toString());
        shiftRepo.save(shift);

    }

    public void setVacation(int startDay, int endDay, User user) {
        Date date;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

        for (int day = startDay; day <= endDay; day++) {
            cal.set(Calendar.DATE, day);
            date = cal.getTime();
            Shift shift = new Shift();
            shift.setUser(user);
            shift.setCreated(date);
            shift.setStatus(AdminStatusShift.VACATION);
            shiftRepo.save(shift);
        }


    }

    public Optional<Shift> getShiftByDateAndId(Date d, long id) {
        return shiftRepo.currentShift(d, id);
    }

    public List<Shift> getShiftsByMonthAndYear(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        Date firstDay = cal.getTime();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        Date endDay = cal.getTime();
        return shiftRepo.getShiftsByMonthAndYear(firstDay, endDay);
    }

    public StatusOpenShift checkOpenShift(Date openDate, User user) {
        Optional<Shift> shift = shiftRepo.currentShift(openDate, user.getId());
        System.out.println(openDate+""+user.getId());
        if (shift.isPresent()) {
            if (shift.get().getStatus() == AdminStatusShift.ABSENTEEISM ||
                    shift.get().getStatus() == AdminStatusShift.BEING_LATE ||
                    shift.get().getStatus() == AdminStatusShift.OK) {
                if (shift.get().getCurrentOpen() == null) {
                    return StatusOpenShift.SHIFT_OK;
                } else {
                    return StatusOpenShift.SHIFT_IS_OPEN;
                }

            } else if (shift.get().getStatus() == AdminStatusShift.SICK_LEAVE) {
                return StatusOpenShift.SHIFT_IS_OPEN_SICK_LEAVE;
            } else if (shift.get().getStatus() == AdminStatusShift.VACATION) {
                return StatusOpenShift.SHIFT_IS_OPEN_VACATION;
            } else {
                return StatusOpenShift.SHIFT_IS_OPEN;
            }

        } else {
            return StatusOpenShift.SHIFT_OK;
        }
    }

    public void closeShift(Date closeDate, User user, boolean autoClose) {
        Shift shift = shiftRepo.currentShift(closeDate, user.getId()).get();
        shift.setClose(closeDate);
        shiftRepo.save(shift);
    }

    public void openShiftForAbsenteeism(Date openDate, User user,String comment) {
        Shift shift = shiftRepo.currentShift(openDate, user.getId()).get();
        String lastComment = shift.getComment();
        StringBuilder stringBuilder = new StringBuilder(lastComment);
        if (lastComment.isEmpty()) {
            stringBuilder.append(comment);
        } else {
            stringBuilder.append("," + comment);
        }
        shift.setComment(stringBuilder.toString());
        shift.setOpen(openDate);
        shiftRepo.save(shift);

    }

    public StatusOpenShift checkCloseShift(Date closeDate, User user) {
        Optional<Shift> shift = shiftRepo.currentShift(closeDate, user.getId());
        if (shift.isPresent()) { ///проверка на существование смены
            if (shift.get().getStatus() == AdminStatusShift.SICK_LEAVE) {
                return StatusOpenShift.SHIFT_IS_OPEN_SICK_LEAVE;
            } else if (shift.get().getStatus() == AdminStatusShift.VACATION) {
                return StatusOpenShift.SHIFT_IS_OPEN_VACATION;
            } else if (shift.get().getCurrentOpen() != null) {
                return StatusOpenShift.SHIFT_OK;
            } else {
                return StatusOpenShift.SHIFT_IS_CLOSE;
            }

        } else {
            return StatusOpenShift.SHIFT_IS_NOT_OPEN;
        }
    }

    public Optional<List<Shift>> optionalShifts(Date start, Date end, long id) {
        return shiftRepo.findAllByIntervalAndUser(start, end, id);
    }

    public Shift findById(long id) {
        return shiftRepo.findById(id).get();
    }

    public AdminStatusShift checkShiftAdmin(Date d, User user) {
        Optional<Shift> optional = shiftRepo.currentShift(d, user.getId());
        if (optional.isPresent()) {
            return optional.get().getStatus();
        } else {
            return AdminStatusShift.ABSENTEEISM_CREATE;
        }
    }

    public void setAbsenteeism(User user, Date d) {

        Shift shift = new Shift();
        shift.setUser(user);
        shift.setStatus(AdminStatusShift.ABSENTEEISM);
        shiftRepo.save(shift);
    }

    public void setTimeOff(User user, Date d) {
        Shift shift = new Shift();
        shift.setUser(user);
        shift.setStatus(AdminStatusShift.TIME_OFF);
        shift.setCreated(d);
        shiftRepo.save(shift);
    }

}

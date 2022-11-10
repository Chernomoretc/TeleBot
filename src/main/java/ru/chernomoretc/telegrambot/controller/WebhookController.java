package ru.chernomoretc.telegrambot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.chernomoretc.telegrambot.entity.LocationUser;
import ru.chernomoretc.telegrambot.entity.Shift;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.model.TelegramBot;
import ru.chernomoretc.telegrambot.services.ServiceLocationUser;
import ru.chernomoretc.telegrambot.services.ServiceShift;
import ru.chernomoretc.telegrambot.services.ServiceUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@RestController
public class WebhookController {
    ServiceShift serviceShift;
    ServiceUser serviceUser;
    ServiceLocationUser serviceLocationUser;
    private final TelegramBot telegramBot;

    public WebhookController(ServiceShift serviceShift, ServiceUser serviceUser, ServiceLocationUser serviceLocationUser, TelegramBot telegramBot) {
        this.serviceShift = serviceShift;
        this.serviceUser = serviceUser;
        this.serviceLocationUser = serviceLocationUser;
        this.telegramBot = telegramBot;

    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/api/shifts")
    public List<Shift> getAllShifts(
                                     @RequestParam(name = "month", required = false) int month,
                                     @RequestParam(name = "year", required = false) int year
    )
    {
        System.out.println(year+" "+month);
        return serviceShift.getShiftsByMonthAndYear(month,year);
    }

    //для будущего api
    @GetMapping("/api")
    public List<Shift> getApi(Date start, Date end) {
        return serviceShift.optionalShifts(new Date(1111111111l), new Date(), 19l).get();
    }

    @GetMapping("/api/users")
    public List<User> getUsers() {
        return serviceUser.findAll();
    }

    @GetMapping("/api/location")
    public List<LocationUser> getLoc(
            @RequestParam(name = "date", required = false)  String date,
            @RequestParam(name = "id", required = false) String id
    ) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d = formatter.parse(date);
        List<LocationUser> locationUsers = serviceLocationUser.getAllByDateAndFullName(Long.parseLong(id),d);
        System.out.println(locationUsers);
        return locationUsers;
    }

    @GetMapping
    public ResponseEntity get() {
        return ResponseEntity.ok().build();
    }
}

package ru.chernomoretc.telegrambot.services;

import org.springframework.stereotype.Service;
import ru.chernomoretc.telegrambot.entity.LocationUser;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.repositories.LocationRepo;
import ru.chernomoretc.telegrambot.repositories.ObjectRepo;
import ru.chernomoretc.telegrambot.repositories.UserRepo;

import java.util.Date;
import java.util.List;

@Service
public class ServiceLocationUser {
    public LocationRepo locationRepo;
    public UserRepo userRepo;

    public ServiceLocationUser(LocationRepo locationRepo, UserRepo userRepo) {
        this.locationRepo = locationRepo;
        this.userRepo = userRepo;
    }

    public void addLocation(Double latitude, Double longitude, Date date,User user) {
        LocationUser locationUser = new LocationUser();
        locationUser.setUser(user);
        locationUser.setDateLoc(date);
        locationUser.setLatitude(latitude);
        locationUser.setLongitude(longitude);
        locationRepo.save(locationUser);

    }

    public List<LocationUser> getAllByDateAndFullName(Long id,Date date)
    {

        return locationRepo.findAllByDateAndId(id,date).get();

    }

    public List<LocationUser> getAll()
    {
        return locationRepo.findAll();
    }
}

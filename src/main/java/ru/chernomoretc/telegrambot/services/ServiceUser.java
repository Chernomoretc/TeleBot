package ru.chernomoretc.telegrambot.services;

import org.springframework.stereotype.Service;
import ru.chernomoretc.telegrambot.entity.User;
import ru.chernomoretc.telegrambot.enumShift.Role;
import ru.chernomoretc.telegrambot.repositories.UserRepo;

import java.util.List;
import java.util.Optional;

@Service

public class ServiceUser {
    public UserRepo userRepo;

    public ServiceUser(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUser(String fullName, Long chatId) {
        Optional<User> optionalUser = findByChatId(chatId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            addUser(fullName, chatId);
            return findByName(fullName).get();
        }
    }
    public void addUser(String fullName,Long chatId) {
        User user = new User();
        user.setFullName(fullName);
        user.setChatId(chatId);
        user.setRole(Role.USER);
        userRepo.save(user);
    }

    public User findById(long id){return userRepo.findById(id).get();}
    public Optional<User> findByName(String fullName)
    {
        return userRepo.findByFullName(fullName);
    }
    public Optional<User> findByChatId(long id)
    {
        return userRepo.findByChatId(id);
    }

    public List <User> findAll()
    {
        return userRepo.findAll();
    }

    public void setRole(long id,Role role)
    {
        User user = userRepo.findByChatId(id).get();
        user.setRole(role);
        userRepo.save(user);
    }
}

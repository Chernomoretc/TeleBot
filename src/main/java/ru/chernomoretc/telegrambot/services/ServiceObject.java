package ru.chernomoretc.telegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chernomoretc.telegrambot.entity.Object;
import ru.chernomoretc.telegrambot.repositories.ObjectRepo;

import java.util.List;
import java.util.Optional;

@Service

public class ServiceObject {
    ObjectRepo objectRepo;

    public ServiceObject(ObjectRepo objectRepo)
    {
        this.objectRepo = objectRepo;
    }

    public void addObject(String name) {
        Object object = new Object();
        object.setObjectName(name);
        objectRepo.save(object);

    }

    public void deleteObject(String name) {
        Object object = objectRepo.findByName(name).get();
        objectRepo.delete(object);

    }
    public List<Object> findAll()
    {
        return objectRepo.findAll();
    }

    public Optional<Object> findByName(String name)
    {
        return objectRepo.findByName(name);
    }
}

package ru.chernomoretc.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.chernomoretc.telegrambot.entity.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select u from user u where full_Name = ?1")
    Optional<User> findByFullName(String fullName);

    @Query("select u from user u where chat_id = ?1")
    Optional<User> findByChatId(long id);
}
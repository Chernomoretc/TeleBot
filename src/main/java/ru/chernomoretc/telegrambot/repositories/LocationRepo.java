package ru.chernomoretc.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.chernomoretc.telegrambot.entity.LocationUser;
import ru.chernomoretc.telegrambot.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepo   extends JpaRepository<LocationUser, Long>, JpaSpecificationExecutor<LocationUser> {

    @Query("select l from location_user l where user_id = ?1 and DATE(date_loc) = ?2")
    Optional<List<LocationUser>> findAllByDateAndId(long id, Date date);
}

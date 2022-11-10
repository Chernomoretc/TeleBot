package ru.chernomoretc.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.chernomoretc.telegrambot.entity.Object;



import java.util.Optional;

@Repository
public interface ObjectRepo extends JpaRepository<Object, Long>, JpaSpecificationExecutor<Object> {
    @Query("select o from object o where object_name = ?1")
    Optional<Object> findByName(String name);
}

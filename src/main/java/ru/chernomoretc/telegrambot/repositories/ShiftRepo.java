package ru.chernomoretc.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.chernomoretc.telegrambot.entity.Shift;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface ShiftRepo extends JpaRepository <Shift, Long>, JpaSpecificationExecutor<Shift> {
//    @Query("select s from shift s where created = ?1 and user_id = ?2")
//    Optional<Shift> currentShift(Date date,long id);


    @Query("select a from shift a where  DATE(created) = ?1 and user_id = ?2")
    Optional<Shift> currentShift(Date date,long id);

    @Query("select a from shift a where  DATE(open) >= ?1 and DATE(close) <= ?2 and user_id = ?3")
    Optional<List<Shift>> findAllByIntervalAndUser(Date start,Date end, long id);

    @Query("select a from shift a where  DATE(created) >= ?1 and DATE(created) <= ?2")
    List<Shift> getShiftsByMonthAndYear(Date start, Date end);


}

package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Planning;
import com.example.schoolgestapp.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPlanning extends JpaRepository<Planning, Long> {
    
    @Query("SELECT p FROM Planning p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.teacher t " +
           "LEFT JOIN FETCH t.user " +
           "LEFT JOIN FETCH p.classe " +
           "LEFT JOIN FETCH p.room " +
           "LEFT JOIN FETCH p.timeSlot " +
           "WHERE p.classe.id = :classeId AND p.date BETWEEN :start AND :end")
    List<Planning> findByClasseIdAndDateBetween(@Param("classeId") Long classeId, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT p FROM Planning p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.teacher t " +
           "LEFT JOIN FETCH t.user " +
           "LEFT JOIN FETCH p.classe " +
           "LEFT JOIN FETCH p.room " +
           "LEFT JOIN FETCH p.timeSlot " +
           "WHERE p.teacher.id = :teacherId AND p.date BETWEEN :start AND :end")
    List<Planning> findByTeacherIdAndDateBetween(@Param("teacherId") Long teacherId, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT p FROM Planning p WHERE p.cancelled = false AND p.date = :date AND p.timeSlot = :slot")
    List<Planning> findActiveByDateAndTimeSlot(@Param("date") LocalDate date, @Param("slot") TimeSlot slot);

    List<Planning> findByRoomIdAndDateAndCancelledFalse(Long roomId, LocalDate date);

    boolean existsByTimeSlotId(Long timeSlotId);
}

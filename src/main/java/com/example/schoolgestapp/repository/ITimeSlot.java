package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITimeSlot extends JpaRepository<TimeSlot, Long> {
}

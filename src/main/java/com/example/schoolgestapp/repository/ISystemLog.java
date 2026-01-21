package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISystemLog extends JpaRepository<SystemLog, Long> {
}

package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISubject extends JpaRepository<Subject, Long> {
    List<Subject> findByModule_Id(Long moduleId);
    List<Subject> findByModule_Classe_Id(Long classeId);
    List<Subject> findByTeacherId(Long teacherId);
}

package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Assignment;
import com.example.schoolgestapp.entity.Classe;
import com.example.schoolgestapp.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssignment extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClasse(Classe classe);
    List<Assignment> findBySubject(Subject subject);
    List<Assignment> findByPublishedTrue();
    List<Assignment> findByTeacherId(Long teacherId);
}

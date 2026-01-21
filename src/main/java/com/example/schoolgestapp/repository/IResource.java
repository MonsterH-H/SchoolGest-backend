package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Resource;
import com.example.schoolgestapp.entity.Subject;
import com.example.schoolgestapp.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IResource extends JpaRepository<Resource, Long> {
    List<Resource> findBySubject(Subject subject);
    List<Resource> findByClasse(Classe classe);
    List<Resource> findByPublishedTrue();
}

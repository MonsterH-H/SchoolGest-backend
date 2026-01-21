package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IModule extends JpaRepository<Module, Long> {
    List<Module> findByClasseId(Long classeId);
}

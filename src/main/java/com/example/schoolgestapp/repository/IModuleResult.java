package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.ModuleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IModuleResult extends JpaRepository<ModuleResult, Long> {
}

package com.example.Student.crud.repository;

import com.example.Student.crud.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectREpository extends JpaRepository<Project,Long> {
}
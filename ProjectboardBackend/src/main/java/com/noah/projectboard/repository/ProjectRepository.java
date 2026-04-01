package com.noah.projectboard.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.noah.projectboard.model.Project;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByTitle(String title);
    List<Project> findByUserId(Long userId);
}
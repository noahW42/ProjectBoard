package com.noah.projectboard.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.noah.projectboard.model.Task;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTitle(String title);
    List<Task> findByProjectId(Long projectId);
}
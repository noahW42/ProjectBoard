package com.noah.projectboard.repository;

import com.noah.projectboard.model.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ColumnRepository extends JpaRepository<Column, Long> {

    // Get all columns for a specific project
    List<Column> findByProjectId(Long projectId);
    List<Column> findByProjectIdOrderByPosition(Long projectId);
}
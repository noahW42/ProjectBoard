package com.noah.projectboard.controller;
import com.noah.projectboard.websocket.SocketEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.noah.projectboard.model.Column;
import com.noah.projectboard.model.Project;
import com.noah.projectboard.repository.ColumnRepository;
import com.noah.projectboard.repository.ProjectRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ColumnController {
    
    private final ColumnRepository columnRepository;
    private final ProjectRepository projectRepository;
    private final SimpMessagingTemplate messagingTemplate;

public ColumnController(ColumnRepository columnRepository,
                        ProjectRepository projectRepository,
                        SimpMessagingTemplate messagingTemplate) {
    this.columnRepository = columnRepository;
    this.projectRepository = projectRepository;
    this.messagingTemplate = messagingTemplate;
   
}


    // Retrieve all columns for a specific project
    @GetMapping("/projects/{projectId}/columns")
    public List<Column> getColumnsByProject(@PathVariable Long projectId) {
        return columnRepository.findByProjectId(projectId);
    }

    // Create a new column under a specific project
    @PostMapping("/projects/{projectId}/columns")
    public Column createColumn(@PathVariable Long projectId, @RequestBody Column column) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        column.setProject(project);
        return columnRepository.save(column);
    }

    // Delete a column by its ID
    @DeleteMapping("/columns/{id}")
    public void deleteColumn(@PathVariable Long id) {
        columnRepository.deleteById(id);
    }
    //update column position within project
  @PutMapping("/projects/{projectId}/columns/reorder")
public void reorderColumns(@PathVariable Long projectId, @RequestBody List<Long> columnIds) {

    for (int i = 0; i < columnIds.size(); i++) {
        Column column = columnRepository.findById(columnIds.get(i))
                .orElseThrow(() -> new RuntimeException("Column not found"));
        column.setPosition(i);
        columnRepository.save(column);
    }
    List<Column> updated = columnRepository
            .findByProjectIdOrderByPosition(projectId);
    messagingTemplate.convertAndSend(
            "/topic/project/" + projectId,
            new SocketEvent("COLUMNS_UPDATED", updated)
    );
}
    // Update an existing column's name
    @PutMapping("/columns/{id}")
    public Column updateColumn(@PathVariable Long id, @RequestBody Column updatedColumn) {
        Column column = columnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        column.setName(updatedColumn.getName());
        return columnRepository.save(column);
    }
}
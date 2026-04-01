package com.noah.projectboard.controller;
import java.util.Map;
import com.noah.projectboard.model.Column;
import com.noah.projectboard.model.Task;
import com.noah.projectboard.repository.ColumnRepository;
import com.noah.projectboard.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskRepository repository;
    private final ColumnRepository columnRepository;

    public TaskController(TaskRepository repository, ColumnRepository columnRepository) {
        this.repository = repository;
        this.columnRepository = columnRepository;
    }
    @GetMapping("/tasks/{id}")
    public Task getTaskById(@PathVariable Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @GetMapping("/tasks/column/{columnId}")
    public List<Task> getTasksByColumnId(@PathVariable Long columnId) {
        return repository.findByColumnId(columnId);
    }

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task newTask) {
        return repository.save(newTask);
    }

    @PostMapping("/columns/{columnId}/tasks")
    public Task createTaskInColumn(@PathVariable Long columnId, @RequestBody Task task) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        task.setColumn(column);
        return repository.save(task);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        return repository.save(task);
    }
    @PutMapping("/tasks/{id}/move")
    public Task moveTask(@PathVariable Long id, @RequestBody Map<String, Long> body) {
    Task task = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    Column column = columnRepository.findById(body.get("columnId"))
            .orElseThrow(() -> new RuntimeException("Column not found"));
    task.setColumn(column);
    return repository.save(task);
}
}
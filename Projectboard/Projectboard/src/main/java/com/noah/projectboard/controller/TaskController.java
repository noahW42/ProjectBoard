package com.noah.projectboard.controller;
import com.noah.projectboard.model.Task;
import com.noah.projectboard.repository.TaskRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController{

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }
@GetMapping
    public List<Task> getAllTasks(){
        return repository.findAll();
    }
@GetMapping("/project/{projectId}")
    public List<Task> getTasksByProjectId(@PathVariable Long projectId){
    return repository.findByProjectId(projectId);
}
@PostMapping
public Task createTask(@RequestBody Task newTask){
    return repository.save(newTask);
}
}
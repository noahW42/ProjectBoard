package com.noah.projectboard.controller;
import com.noah.projectboard.model.Project;
import com.noah.projectboard.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController{

    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }
@GetMapping
    public List<Project> getAllProjects(){
        return repository.findAll();
    }
@GetMapping("/user/{userId}")
    public List<Project> getProjectsByUserId(@PathVariable Long userId){
    return repository.findByUserId(userId);
}
@PostMapping
public Project createProject(@RequestBody Project newProject){
    return repository.save(newProject);
}
}
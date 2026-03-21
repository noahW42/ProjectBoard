package com.noah.projectboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String title;
private String description;

@ManyToOne
@JoinColumn(name = "project_id")
@Jsonignore
private Project project;

public Task() {}
    public Task(String title, String description, Project project ) {
        this.title = title;
        this.description = description;
        this.project = project;
    }
public Long getId() {return id;}
public String getTitle() {return title;}
public String getDescription() {return description;}
public Project getProject() {return project;}

public void setTitle(String title) {
    this.title = title;
}

public void setDescription(String description) {
    this.description = description;
}
public void setProject(Project project) {
    this.project = project;
}
}
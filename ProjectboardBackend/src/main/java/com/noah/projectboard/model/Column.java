package com.noah.projectboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "columns")
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int position;
    // Many columns belong to one project
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    // One column has many tasks
    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    private List<Task> tasks;

    public Column() {}

    public Column(String name, Project project) {
        this.name = name;
        this.project = project;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Project getProject() {
        return project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
public int getPosition() {
     return position;
      }
public void setPosition(int position) {
     this.position = position;
      }
}
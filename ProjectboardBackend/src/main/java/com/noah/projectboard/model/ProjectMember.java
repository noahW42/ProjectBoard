package com.noah.projectboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_members")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ProjectMember() {}

    public ProjectMember(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public User getUser() { return user; }
    public void setProject(Project project) { this.project = project; }
    public void setUser(User user) { this.user = user; }
}
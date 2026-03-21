package com.noah.projectboard.model;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Project {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String title;
private String description;
@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> tasks;
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
public Project() {}
    public Project(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }
public Long getId() {return id;}
public String getTitle() {return title;}
public String getDescription() {return description;}
public User getUser() {return user;}
public List<Task> getTasks() { return tasks; }

public void setTitle(String title) {
    this.title = title;
}

public void setDescription(String description) {
    this.description = description;
}

public void setUser(User user) {
    this.user = user;
}
}

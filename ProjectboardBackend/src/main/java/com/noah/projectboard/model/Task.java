package com.noah.projectboard.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JoinColumn(name = "column_id")
@JsonIgnore
private Column column;

public Task() {}
    public Task(String title, String description, Column column) {
        this.title = title;
        this.description = description;
        this.column = column;
    }
public Long getId() {return id;}
public String getTitle() {return title;}
public String getDescription() {return description;}
public Column getColumn() {return column;}

public void setTitle(String title) {
    this.title = title;
}

public void setDescription(String description) {
    this.description = description;
}
public void setColumn(Column column) {
    this.column = column;
}
}
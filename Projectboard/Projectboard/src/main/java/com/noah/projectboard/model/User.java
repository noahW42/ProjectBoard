package com.noah.projectboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String username;
private String password;
private String email;
public void setId(Long id) {
    this.id = id;
}
public User() {}
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
public Long getId() {
    return id;
}
public String getUsername() {
    return username;
}
public String getPassword() {
    return password;
}

public String getEmail() {
    return email;
}
}
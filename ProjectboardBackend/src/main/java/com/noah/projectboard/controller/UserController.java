package com.noah.projectboard.controller;
import com.noah.projectboard.model.User;
import com.noah.projectboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController{

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }
@GetMapping
    public List<User> getAllUsers(){
        return repository.findAll();
    }
@PostMapping
public User createUser(@RequestBody User newUser){
    return repository.save(newUser);
}
}
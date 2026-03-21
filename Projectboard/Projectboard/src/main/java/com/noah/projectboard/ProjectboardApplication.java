package com.noah.Projectboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@SpringBootApplication
public class ProjectboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectboardApplication.class, args);
	}
	
	

}

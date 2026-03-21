package com.noah.projectboard.controller;
import com.noah.projectboard.model.Project;
import com.noah.projectboard.model.User;
import com.noah.projectboard.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
@Controller
public class PageController {

    private final ProjectRepository projectRepository;

    public PageController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

@GetMapping("/projects")
public String getProjectsPage(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");
    if (user == null) return "redirect:/login";

    model.addAttribute("projects", projectRepository.findAll());
    model.addAttribute("project", new Project());
    model.addAttribute("currentUser", user);
    return "projects";
}

@PostMapping("/projects")
public String createProject(@RequestParam String title, 
                            @RequestParam String description, 
                            HttpSession session) {
    User user = (User) session.getAttribute("user");
    if (user == null) return "redirect:/login";

    Project project = new Project(title, description, user);
    projectRepository.save(project);
    return "redirect:/projects";
}
}
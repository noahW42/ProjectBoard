package com.noah.projectboard.controller;

import com.noah.projectboard.model.Project;
import com.noah.projectboard.model.ProjectMember;
import com.noah.projectboard.model.User;
import com.noah.projectboard.repository.ProjectMemberRepository;
import com.noah.projectboard.repository.ProjectRepository;
import com.noah.projectboard.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProjectController {

    private final ProjectRepository repository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository repository,
                             ProjectMemberRepository memberRepository,
                             UserRepository userRepository) {
        this.repository = repository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // Get all projects for current user (owned + member of)
    @GetMapping
    public List<Project> getMyProjects(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) throw new RuntimeException("Not logged in");

        // Projects they own
        List<Project> projects = new ArrayList<>(repository.findByUserId(user.getId()));

        // Projects they are a member of
        List<ProjectMember> memberships = memberRepository.findByUserId(user.getId());
        for (ProjectMember membership : memberships) {
            projects.add(membership.getProject());
        }

        return projects;
    }

    // Get a single project by ID
    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    // Create a new project
    @PostMapping
    public Project createProject(@RequestBody Project newProject, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) throw new RuntimeException("Not logged in");
        newProject.setUser(user);
        return repository.save(newProject);
    }

    // Invite a user to a project by username
    @PostMapping("/{id}/invite")
    public String inviteUser(@PathVariable Long id,
                             @RequestBody Map<String, String> body,
                             HttpSession session) {
        User owner = (User) session.getAttribute("user");
        Project project = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(owner.getId())) {
            throw new RuntimeException("Only the owner can invite users");
        }

        String username = body.get("username");
        User invitee = userRepository.findByUsername(username);
        if (invitee == null) throw new RuntimeException("User not found");

        if (memberRepository.existsByProjectIdAndUserId(id, invitee.getId())) {
            throw new RuntimeException("User is already a member");
        }

        memberRepository.save(new ProjectMember(project, invitee));
        return "User invited successfully";
    }

    // Remove a user from a project
    @DeleteMapping("/{id}/members/{userId}")
    public String removeMember(@PathVariable Long id,
                               @PathVariable Long userId,
                               HttpSession session) {
        User owner = (User) session.getAttribute("user");
        Project project = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(owner.getId())) {
            throw new RuntimeException("Only the owner can remove members");
        }

        memberRepository.deleteByProjectIdAndUserId(id, userId);
        return "Member removed";
    }

    // Get all members of a project
    @GetMapping("/{id}/members")
    public List<User> getMembers(@PathVariable Long id) {
        return memberRepository.findByProjectId(id)
                .stream()
                .map(ProjectMember::getUser)
                .toList();
    }
}
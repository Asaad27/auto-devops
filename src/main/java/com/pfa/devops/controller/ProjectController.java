package com.pfa.devops.controller;


import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.pfa.devops.jenkins.JenkinsJob;
import com.pfa.devops.model.Project;
import com.pfa.devops.model.User;
import com.pfa.devops.service.ProjectService;
import com.pfa.devops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.Set;

@Controller
public class ProjectController {
	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;

	private User currentUser;

	@GetMapping("/projectList")
	public String viewHomePage(Model model) {

		this.currentUser = UserController.current_user;
		int currentId = currentUser.getUser_id();
		Set<Project> projectSet = userService.findProjectsByUserId(currentId);


		model.addAttribute("projectList", projectSet);

		return "project-list";

	}



	@GetMapping("/deleteProject/{id}")
	public String deleteProject(@PathVariable(value = "id") int id) {

		userService.deleteProject(currentUser.getUser_id(), projectService.findById(id));

		return "redirect:/projectList";
	}



}

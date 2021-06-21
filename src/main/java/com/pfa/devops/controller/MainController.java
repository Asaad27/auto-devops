package com.pfa.devops.controller;



import com.pfa.devops.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

	@GetMapping("/processProject")
	public String greetingForm(Model model) {
		model.addAttribute("project", new Project());
		return "application-form";
	}

	@PostMapping("/processProject")
	public String greetingSubmit(@ModelAttribute Project project, Model model) {
		System.out.println(project.getProject_title());
		model.addAttribute("project", project);
		System.out.println(project.getProject_language());
		System.out.println(project.getProject_docker_deployment());
		System.out.println(project.getProject_model_training());
		return "result";
	}

}
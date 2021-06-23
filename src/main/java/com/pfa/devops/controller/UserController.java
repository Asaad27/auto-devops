package com.pfa.devops.controller;

import com.pfa.devops.model.Project;
import com.pfa.devops.model.User;
import com.pfa.devops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserController {

	public static User current_user;

	@Autowired
	private UserService userService;
	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("user", new User());
		return "register-form";
	}


	@PostMapping("/register")
	public String registerPost(@ModelAttribute User user, Model model) {

		userService.create(user);

		return "redirect:/Login";
	}

	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("user", new User());

		return "login";
	}

	@GetMapping("/")
	public String loginFormRoot(Model model) {
		model.addAttribute("user", new User());
		return "login";
	}

	@PostMapping("/login")
	public String loginSubmit(@ModelAttribute User user, Model model) {

		String userName = user.getUser_name();
		String userPassword = user.getUser_password();
		boolean flag = userService.checkLogging(userName, userPassword);
		if (flag)
			current_user = userService.findByName(userName);

		if (flag){
			model.addAttribute("project", new Project());
			return "application-form";
		}

		return "login";
	}



}
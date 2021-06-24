package com.pfa.devops.controller;


import com.offbytwo.jenkins.JenkinsServer;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

		JenkinsServer jenkins = null;
		try {
			jenkins = new JenkinsServer(new URI(JenkinsJob.JENKINS_URI), JenkinsJob.USERNAME, JenkinsJob.PASSWORD);
		} catch (URISyntaxException e) {
			System.err.println(e);
		}

		for (Project project : projectSet){
			try {
				assert jenkins != null;
				Map<String, Job> jb = jenkins.getJobs();
				Build build = null;
				if( jb.get(project.getProject_title()) != null){
					JobWithDetails jobWithDetails = jb.get(project.getProject_title()).details();
					build = jobWithDetails.getLastBuild();
				}

				if(build != null && build.details() != null && build.details().getResult() != null){
					project.setLastBuild(build.details().getResult().toString());
					projectService.update(project);
				}
				else
				{
					project.setLastBuild("ABORTED");
					projectService.update(project);
				}

			} catch (IOException e) {
				System.err.println(e);

			}
			finally {
			}
		}

		model.addAttribute("projectList", projectSet);

		return "project-list";

	}



	@GetMapping("/deleteProject/{id}")
	public String deleteProject(@PathVariable(value = "id") int id) {

		userService.deleteProject(currentUser.getUser_id(), projectService.findById(id));

		return "redirect:/projectList";
	}



}

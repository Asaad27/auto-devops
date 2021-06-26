package com.pfa.devops.controller;


import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import com.pfa.devops.jenkins.CustomPair;
import com.pfa.devops.jenkins.JenkinsJob;
import com.pfa.devops.model.Project;
import com.pfa.devops.model.User;
import com.pfa.devops.service.ProjectService;
import com.pfa.devops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


@Controller
public class MainController {
	private static String XMLPATH = "src/main/java/com/pfa/devops/jenkins/jobTemplates/job";
	public Project project;
	public List<CustomPair> artifacts;
	public JenkinsServer jenkins;
	public JenkinsJob job;
	public boolean isRunning = true;
	public boolean isNotFinished = true;
	Map<String, Job> jobs;


	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	private Project buildProject;
	private Boolean isFromBuild = false;

	@GetMapping("/buildProject/{id}")
	public String buildParamProject(@PathVariable(value = "id") int id, Model model) {

		buildProject = projectService.findById(id);
		buildProject.setProject_statue("Not started");
		isFromBuild = true;

		return "redirect:/processProject";
	}

	@GetMapping("/processProject")
	public String projectForm(Model model) {

		if (!isFromBuild)
			model.addAttribute("project", new Project());
		else{
			model.addAttribute("project", buildProject);
			isFromBuild = false;
		}

		return "application-form";
	}

	@PostMapping("/resultLoading")
	public String projectSubmit(@ModelAttribute Project project, Model model) {
		boolean projectExist = (projectService.findByName(project.getProject_title()) != null);
		if (!projectExist) {
			projectService.create(project);
			project.setProject_title(project.getProject_title() + project.getProject_id());
			project.find_project_Type();
			projectService.update(project);
			userService.addProject(UserController.current_user.getUser_id(), project);
			UserController.current_user = userService.findById(UserController.current_user.getUser_id());
		}
		else
			project = projectService.findByName(project.getProject_title());

		//check project type again
		isRunning = true;
		project.find_project_Type();
		project.setProject_statue("BUILDING");
		this.project = project;
		artifacts = new ArrayList<>();
		model.addAttribute("project", project);
		model.addAttribute("artifacts", artifacts);
		model.addAttribute("isNotFinished", isNotFinished);
		model.addAttribute("isRunning", true);

		createProject();
		buildParamProject();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return "result-loading";
	}

	@GetMapping("/resultLoading")
	public String resultGet(Model model) {

		model.addAttribute("project", project);
		model.addAttribute("isRunning",isRunning);
		model.addAttribute("artifacts", artifacts);

		if (!isRunning && !project.getProject_statue().equals("BUILD FINISHED")) {
			getArtifacts();
			project.setProject_statue("BUILD FINISHED");
			model.addAttribute("artifacts", artifacts);
			model.addAttribute("isNotFinished", false);


		}
		if (isRunning) {
			try {
				jobs = jenkins.getJobs();
				JobWithDetails jobWithDetails = jobs.get(project.getProject_title()).details();
				jobWithDetails.updateDescription(project.getProject_description());
				Build build = jobWithDetails.getLastBuild();
				isRunning = build.details().isBuilding();
				Build nextBuild = job.details().getBuildByNumber(jobWithDetails.getNextBuildNumber());
				if (nextBuild != null)
					isRunning = isRunning || nextBuild.details().isBuilding();

				System.out.println("is running " + isRunning);
				System.out.println("next build " + jobWithDetails.getNextBuildNumber());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return "result-loading";
	}



	public void createProject() {

		try {
			this.jenkins = new JenkinsServer(new URI(JenkinsJob.JENKINS_URI), JenkinsJob.USERNAME, JenkinsJob.PASSWORD);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		job = new JenkinsJob(project.getProject_title(), XMLPATH+project.getProject_type()+".xml");
		job.createJob();
	}

	public void buildParamProject() {
		// set parameters
		Map<String, List<String>> params = new HashMap<>();
		List<String> paramList1 = new ArrayList<>();
		List<String> paramList2 = new ArrayList<>();

		JenkinsClient client = JenkinsClient.builder()
				.endPoint(JenkinsJob.JENKINS_URI) //
				.credentials(JenkinsJob.USERNAME + ":" + JenkinsJob.PASSWORD) // Optional.
				.build();
		//SystemInfo systemInfo = client.api().systemApi().systemInfo();

		paramList1.add(project.getProject_github_repo());
		paramList2.add(project.getProject_model_accuracy());
		params.put("GITHUB", paramList1);
		params.put("FLAG", paramList2);

		// sending request
		IntegerResponse response = client.api().jobsApi()
				.buildWithParameters(null, project.getProject_title(), params);
	}

	public void getArtifacts() {
		try {
			jobs = jenkins.getJobs();
			JobWithDetails jobWithDetails = jobs.get(project.getProject_title()).details();
			Build build = jobWithDetails.getLastBuild();
			List<Artifact> my_artifacts = build.details().getArtifacts();
			for (Artifact art : my_artifacts) {
				URI uri = new URI(build.getUrl());
				String artifactPath = uri.getPath() + "artifact/" + art.getRelativePath();
				URI artifactUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), artifactPath, "", "");
				System.out.println(artifactUri);
				artifacts.add(new CustomPair(art.getFileName(), artifactUri.toString()));
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
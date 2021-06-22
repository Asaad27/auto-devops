package com.pfa.devops.controller;


import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.pfa.devops.jenkins.CustomPair;
import com.pfa.devops.jenkins.JenkinsJob;
import com.pfa.devops.model.Project;
import com.pfa.devops.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class MainController {
	private static final String XMLPATH = "src/main/java/com/pfa/devops/jenkins/job1.xml";
	public Project project;
	public List<CustomPair> artifacts;
	public JenkinsServer jenkins;
	public JenkinsJob job;
	public boolean isRunning = true;
	public boolean isNotFinished = true;
	Map<String, Job> jobs;
	@Autowired
	private ProjectService projectService;

	@GetMapping("/processProject")
	public String projectForm(Model model) {
		model.addAttribute("project", new Project());
		return "application-form";
	}

	@PostMapping("/resultLoading")
	public String projectSubmit(@ModelAttribute Project project, Model model) {

		System.out.println("post");

		projectService.create(project);
		System.out.println("PROJECT ID : " + project.getProject_id());
		project.setProject_title(project.getProject_title() + project.getProject_id());
		projectService.update(project);
		project.setProject_statue("BUILDING");
		this.project = project;
		artifacts = new ArrayList<>();
		model.addAttribute("project", project);
		model.addAttribute("artifacts", artifacts);
		model.addAttribute("isNotFinished", isNotFinished);
		model.addAttribute("isRunning", true);

		createProject();
		buildProject();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return "result-loading";
	}

	@GetMapping("/resultLoading")
	public String resultGet(Model model) {

		System.out.println("get");
		System.out.println(this.project.getProject_title());
		model.addAttribute("project", project);
		model.addAttribute("isRunning", this.isRunning);
		model.addAttribute("artifacts", artifacts);
		System.out.println("from get : running " + this.isRunning);
		if (!isRunning && !project.getProject_statue().equals("BUILD FINISHED")) {
			System.out.println("in get artifacts");
			getArtifacts();
			project.setProject_statue("BUILD FINISHED");
			System.out.println("our from get artifacts");
			model.addAttribute("artifacts", artifacts);
			projectService.update(project);
			model.addAttribute("isNotFinished", false);

		}

		if (isRunning) {
			try {
				jobs = jenkins.getJobs();
				JobWithDetails jobWithDetails = jobs.get(project.getProject_title()).details();
				jobWithDetails.updateDescription(project.getProject_description());
				Build build = jobWithDetails.getLastBuild();
				isRunning = build.details().isBuilding();
			} catch (IOException e) {
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
		job = new JenkinsJob(project.getProject_title(), XMLPATH);
		job.createJob();
	}

	public void buildProject() {
		// set parameters
		Map<String, List<String>> params = new HashMap<>();
		List<String> paramList1 = new ArrayList<>();
		List<String> paramList2 = new ArrayList<>();

		JenkinsClient client = JenkinsClient.builder()
				.endPoint(JenkinsJob.JENKINS_URI) //
				.credentials(JenkinsJob.USERNAME + ":" + JenkinsJob.PASSWORD) // Optional.
				.build();
		SystemInfo systemInfo = client.api().systemApi().systemInfo();
		System.out.println(systemInfo);

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
			Map<String, Job> jobs = jenkins.getJobs();
			JobWithDetails jobWithDetails = jobs.get(project.getProject_title()).details();
			Build build = jobWithDetails.getLastBuild();
			List<Artifact> my_artifacts = build.details().getArtifacts();
			for (Artifact art : my_artifacts) {
				System.out.println(art.getFileName());
				System.out.println(art.getDisplayPath());
				System.out.println(art.getRelativePath());
				System.out.println("downloading");
				InputStream inputStream = build.details().downloadArtifact(art);
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
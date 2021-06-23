package com.pfa.devops.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project")
public class Project {
	@Column(name = "project_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Integer project_id;

	private String project_title;

	private String project_github_repo;

	private String project_language; //python, java, react

	private String project_jenkins_uri;

	private String project_slack_id;

	private String project_email;

	private String project_description;

	private String project_statue ;

	private Boolean project_model_training;

	private String project_model_accuracy;

	private Boolean project_unit_testing = false;

	private Boolean project_docker_deployment;

	private Boolean project_aws_deployment;

	private String lastBuild;

	private int project_type; //0, is model training, 1 modeltraining+docker, 3....

	@ManyToMany(mappedBy = "projects")
	private Set<User> users = new HashSet<>();


	public int find_project_Type(){
		if (project_language.equals("python")){
			if(project_model_training && project_unit_testing && project_docker_deployment && project_aws_deployment)
				this.project_type = 4;
			else if (project_model_training && project_unit_testing && project_docker_deployment)
				this.project_type = 3;
			else if (project_model_training && project_unit_testing)
				this.project_type = 2;
			else if (project_docker_deployment && project_unit_testing)
				this.project_type = 5;
			else if (project_aws_deployment && project_docker_deployment)
				this.project_type = 8;
			else if (project_docker_deployment)
				this.project_type = 6;
			else if (project_model_training)
				this.project_type = 1;
			else if (project_aws_deployment)
				this.project_type = 7;
		}
		else{
			if (project_aws_deployment)
				this.project_type = 9;
			else
				this.project_type = 10;
		}

		return project_type;
	}

	@Override
	public String toString() {
		return project_id + " type " + project_type + " docker? " + project_docker_deployment + " lang " + project_language + " title " + project_title;
	}

	public String getProject_title() {
		return project_title;
	}

	public void setProject_id(Integer project_id) {
		this.project_id = project_id;
	}

	public Integer getProject_id() {
		return project_id;
	}

	public void setProject_title(String project_title) {
		this.project_title = project_title;
	}

	public String getProject_github_repo() {
		return project_github_repo;
	}

	public void setProject_github_repo(String project_github_repo) {
		this.project_github_repo = project_github_repo;
	}

	public String getProject_language() {
		return project_language;
	}

	public void setProject_language(String project_language) {
		this.project_language = project_language;
	}

	public String getProject_jenkins_uri() {
		return project_jenkins_uri;
	}

	public void setProject_jenkins_uri(String project_jenkins_uri) {
		this.project_jenkins_uri = project_jenkins_uri;
	}

	public String getProject_slack_id() {
		return project_slack_id;
	}

	public void setProject_slack_id(String project_slack_id) {
		this.project_slack_id = project_slack_id;
	}

	public String getProject_email() {
		return project_email;
	}

	public void setProject_email(String project_email) {
		this.project_email = project_email;
	}

	public String getProject_description() {
		return project_description;
	}

	public void setProject_description(String project_description) {
		this.project_description = project_description;
	}

	public String getProject_statue() {
		return project_statue;
	}

	public void setProject_statue(String project_statue) {
		this.project_statue = project_statue;
	}

	public Boolean getProject_model_training() {
		return project_model_training;
	}

	public void setProject_model_training(Boolean project_model_training) {
		this.project_model_training = project_model_training;
	}

	public String getProject_model_accuracy() {
		return project_model_accuracy;
	}

	public void setProject_model_accuracy(String project_model_accuracy) {
		this.project_model_accuracy = project_model_accuracy;
	}

	public Boolean getProject_unit_testing() {
		return project_unit_testing;
	}

	public void setProject_unit_testing(Boolean project_unit_testing) {
		this.project_unit_testing = project_unit_testing;
	}

	public Boolean getProject_docker_deployment() {
		return project_docker_deployment;
	}

	public void setProject_docker_deployment(Boolean project_docker_deployment) {
		this.project_docker_deployment = project_docker_deployment;
	}

	public Boolean getProject_aws_deployment() {
		return project_aws_deployment;
	}

	public void setProject_aws_deployment(Boolean project_aws_deployment) {
		this.project_aws_deployment = project_aws_deployment;
	}

	public int getProject_type() {
		return project_type;
	}

	public void setProject_type(int project_type) {
		this.project_type = project_type;
	}

	public Set<User> getUsers() {
		return users;
	}

	public String getLastBuild() {
		return lastBuild;
	}

	public void setLastBuild(String lastBuild) {
		this.lastBuild = lastBuild;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}

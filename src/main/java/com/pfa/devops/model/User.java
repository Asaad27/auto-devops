package com.pfa.devops.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
	@Column(name = "user_id", nullable = false)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer user_id;

	private String user_name;

	private String user_password;

	private String user_email;

	private String user_slack_id;

	private String user_github_id;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(
			name = "user_project",
			joinColumns = { @JoinColumn(name = "user_id") },
			inverseJoinColumns = { @JoinColumn(name = "project_id") }
	)
	Set<Project> projects = new HashSet<>();

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_slack_id() {
		return user_slack_id;
	}

	public void setUser_slack_id(String user_slack_id) {
		this.user_slack_id = user_slack_id;
	}

	public String getUser_github_id() {
		return user_github_id;
	}

	public void setUser_github_id(String user_github_id) {
		this.user_github_id = user_github_id;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public User(String user_name, String user_password) {
		this.user_name = user_name;
		this.user_password = user_password;
	}

	public User() {
	}
}

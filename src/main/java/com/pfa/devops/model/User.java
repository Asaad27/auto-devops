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


}

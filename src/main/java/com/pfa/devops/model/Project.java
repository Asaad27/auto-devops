package com.pfa.devops.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
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

	private String project_docker_repo;

	private String project_language; //python, java, react

	private String project_jenkins_uri;

	private String project_email;

	private String project_description;

	private Boolean project_statue;

	private Boolean project_model_training;

	private int project_model_accuracy;

	private Boolean project_unit_testing;

	private Boolean project_docker_deployment;

	private Boolean project_aws_deployment;

	private int project_type; //0, is model training, 1 modeltraining+docker, 3....

	@ManyToMany(mappedBy = "projects")
	private Set<User> users = new HashSet<>();



}

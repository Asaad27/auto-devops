package com.pfa.devops.service;

import com.pfa.devops.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
	Project findById(int id);

	List<Project> findAll();

	Project findByName(String name);

	void update(Project project);
	void create(Project project);
	void delById(int id);
	void delByName(String name);
}

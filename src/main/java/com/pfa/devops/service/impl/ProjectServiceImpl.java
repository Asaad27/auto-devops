package com.pfa.devops.service.impl;

import com.pfa.devops.model.Project;
import com.pfa.devops.repository.ProjectRepository;
import com.pfa.devops.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Override
	public Project findById(int id) {
		Optional<Project> project = projectRepository.findById(id);
		return project.orElse(null);

	}

	@Override
	public List<Project> findAll() {
		return projectRepository.findAll();
	}

	@Override
	public Project findByName(String name) {
		List<Project> projectList = findAll();

		for (Project project : projectList){
			if (project.getProject_title().equals(name))
				return project;
		}
		return null;
	}

	@Override
	public void update(Project project) {
		projectRepository.save(project);
	}

	@Override
	public void create(Project project) {
		projectRepository.save(project);
	}

	@Override
	public void delById(int id) {
		Project project = findById(id);
		if (project != null)
			projectRepository.delete(project);
	}

	@Override
	public void delByName(String name) {
		Project project = findByName(name);
		if (project != null)
			projectRepository.delete(project);

	}
}

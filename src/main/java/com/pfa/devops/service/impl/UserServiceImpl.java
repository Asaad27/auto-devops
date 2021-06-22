package com.pfa.devops.service.impl;

import com.pfa.devops.model.Project;
import com.pfa.devops.model.User;
import com.pfa.devops.repository.UserRepository;
import com.pfa.devops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;

	@Override
	public Set<Project> findProjectsByUserId(int id) {
		User user = userRepository.findById(id).orElseThrow();
		return user.getProjects();
	}

	@Override
	public User findById(int id) {
		return userRepository.findById(id).orElseThrow();
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User findByName(String name) {
		List<User> users = userRepository.findAll();
		for (User user : users){
			if (user.getUser_name().equals(name))
				return user;
		}

		return null;
	}

	@Override
	public void update(User user) {
		userRepository.save(user);
	}

	@Override
	public void create(User user) {
		userRepository.save(user);
	}

	@Override
	public void delById(int id) {
		userRepository.delete(userRepository.getById(id));
	}

	@Override
	public void delByName(String name) {
		userRepository.delete(findByName(name));
	}

	@Override
	public Boolean checkLogging(String username, String password) {
		User user = findByName(username);
		if (user == null)
			return false;

		return user.getUser_password().equals(password);
	}



	@Override
	public void addProject(int userId, Project project) {
		User user = findById(userId);
		if (user == null)
			return;
		Set<Project> projects = user.getProjects();
		projects.add(project);
		user.setProjects(projects);

		userRepository.save(user);
	}

	@Override
	public void deleteProject(int userId, Project project) {
		User user = findById(userId);
		if (user == null)
			return;
		Set<Project> projects = user.getProjects();
		projects.remove(project);
		user.setProjects(projects);

		userRepository.save(user);

	}
}

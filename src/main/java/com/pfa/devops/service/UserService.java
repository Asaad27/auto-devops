package com.pfa.devops.service;



import com.pfa.devops.model.Project;
import com.pfa.devops.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
	Set<Project> findProjectsByUserId(int id);
	User findById(int id);
	List<User> findAll();
	User findByName(String name);
	void update(User user);
	void create(User user);
	void delById(int id);
	void delByName(String name);
	Boolean checkLogging(String username, String password);
	void addProject(int userId, Project project);
	void deleteProject(int userId, Project project);

}

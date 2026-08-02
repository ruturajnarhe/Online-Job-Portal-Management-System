package com.jsp.ojpms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jsp.ojpms.entity.User;
import com.jsp.ojpms.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository ;
	}
	
	public User createUser(User user) {
		return userRepository.save(user);
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
}

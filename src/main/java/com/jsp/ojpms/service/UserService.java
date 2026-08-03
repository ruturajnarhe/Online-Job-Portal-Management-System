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
	
	// Register user
	public User createUser(User user) {
		return userRepository.save(user);
	}
	
	// Get all the users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	// Get User by id
	public User getUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}
	
	// delete user by id
	public void deleteUser(int id) {
		userRepository.deleteById(id);
	}
	
	// update user
	public User updateUser(int id, User user) {
		User existingUser = userRepository.findById(id).orElse(null);
		
		if(existingUser ==  null) {
			return null;
		}
		
		existingUser.setName(user.getName());
		existingUser.setEmail(user.getEmail());
		existingUser.setPassword(user.getPassword());
		existingUser.setRole(user.getRole());
		
		return userRepository.save(existingUser);
	}
	
}

package com.jsp.ojpms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.ojpms.entity.User;
import com.jsp.ojpms.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private final UserService userService ;
	
	public UserController(UserService userServise) {
		this.userService = userServise ;
	}
	
	@PostMapping
	public User createUser(@RequestBody User user) {
		
//		System.out.println("USER = " + user);
//	    System.out.println("NAME = " + user.getName());
//	    System.out.println("EMAIL = " + user.getEmail());
//	    System.out.println("PASSWORD = " + user.getPassword());
//	    System.out.println("ROLE = " + user.getRole());
		
		return userService.createUser(user);
	}
	
	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public User getUserById(@PathVariable int id) {
		return userService.getUserById(id);
	}
	
	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable int id) {
		userService.deleteUser(id);
		return "User deleted successfully";
	}
	
	@PutMapping("/{id}")
	public User updateUser(@PathVariable int id , @RequestBody User user) {
		return userService.updateUser(id, user);
	}
}

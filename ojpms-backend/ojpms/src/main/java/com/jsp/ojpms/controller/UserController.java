package com.jsp.ojpms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.ojpms.dto.LoginResponse;
import com.jsp.ojpms.dto.RegisterRequest;
import com.jsp.ojpms.dto.UserResponse;
import com.jsp.ojpms.entity.User;
import com.jsp.ojpms.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =====================================================
    // REGISTER
    // PUBLIC
    // =====================================================

    @PostMapping
    public UserResponse createUser(
            @RequestBody RegisterRequest request) {

        return userService.createUser(request);
    }

    // =====================================================
    // LOGIN
    // PUBLIC
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestParam String email,
            @RequestParam String password) {

        LoginResponse response =
                userService.loginUser(
                        email,
                        password
                );

        if (response == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // GET ALL USERS
    // AUTHENTICATED
    // =====================================================

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    // =====================================================
    // GET USER BY ID
    // ONLY OWN ACCOUNT
    // =====================================================

    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable int id,
            Authentication authentication) {

        String loggedInEmail =
                authentication.getName();

        return userService.getUserById(
                id,
                loggedInEmail
        );
    }

    // =====================================================
    // DELETE USER
    // ONLY OWN ACCOUNT
    // =====================================================

    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable int id,
            Authentication authentication) {

        String loggedInEmail =
                authentication.getName();

        userService.deleteUser(
                id,
                loggedInEmail
        );

        return "User deleted successfully";
    }

    // =====================================================
    // UPDATE USER
    // ONLY OWN ACCOUNT
    // =====================================================

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable int id,
            @RequestBody User user,
            Authentication authentication) {

        String loggedInEmail =
                authentication.getName();

        return userService.updateUser(
                id,
                user,
                loggedInEmail
        );
    }
}
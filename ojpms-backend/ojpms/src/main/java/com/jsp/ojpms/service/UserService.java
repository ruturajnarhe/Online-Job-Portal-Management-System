package com.jsp.ojpms.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jsp.ojpms.dto.LoginResponse;
import com.jsp.ojpms.dto.RegisterRequest;
import com.jsp.ojpms.dto.UserResponse;
import com.jsp.ojpms.entity.User;
import com.jsp.ojpms.exception.UserNotFoundException;
import com.jsp.ojpms.repository.UserRepository;
import com.jsp.ojpms.security.JwtService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =====================================================
    // REGISTER USER
    // PUBLIC
    // =====================================================

    public UserResponse createUser(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "User details are required"
            );
        }

        // -------------------------------------------------
        // Validate name
        // -------------------------------------------------

        if (request.getName() == null
                || request.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Name is required"
            );
        }

        // -------------------------------------------------
        // Validate email
        // -------------------------------------------------

        if (request.getEmail() == null
                || request.getEmail().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        // -------------------------------------------------
        // Validate password
        // -------------------------------------------------

        if (request.getPassword() == null
                || request.getPassword().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        // -------------------------------------------------
        // Validate role
        // -------------------------------------------------

        if (request.getRole() == null
                || (
                    !request.getRole().equalsIgnoreCase("RECRUITER")
                    && !request.getRole().equalsIgnoreCase("JOB_SEEKER")
                )) {

            throw new IllegalArgumentException(
                    "Role must be either RECRUITER or JOB_SEEKER"
            );
        }

        // -------------------------------------------------
        // Create User entity
        // -------------------------------------------------

        User user = new User();

        user.setName(request.getName().trim());

        user.setEmail(request.getEmail().trim());

        user.setRole(
                request.getRole().toUpperCase()
        );

        // -------------------------------------------------
        // BCrypt password
        // -------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // -------------------------------------------------
        // Save user
        // -------------------------------------------------

        User savedUser = userRepository.save(user);

        // -------------------------------------------------
        // Return safe response
        // -------------------------------------------------

        return new UserResponse(savedUser);
    }

    // =====================================================
    // GET ALL USERS
    // AUTHENTICATED
    // =====================================================

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    // =====================================================
    // GET USER BY ID
    // ONLY OWN ACCOUNT
    // =====================================================

    public UserResponse getUserById(
            int id,
            String loggedInEmail) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id : " + id
                        )
                );

        validateUserOwnership(
                user,
                loggedInEmail
        );

        return new UserResponse(user);
    }

    // =====================================================
    // DELETE USER
    // ONLY OWN ACCOUNT
    // =====================================================

    public void deleteUser(
            int id,
            String loggedInEmail) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id : " + id
                        )
                );

        validateUserOwnership(
                user,
                loggedInEmail
        );

        userRepository.delete(user);
    }

    // =====================================================
    // UPDATE USER
    // ONLY OWN ACCOUNT
    // =====================================================

    public UserResponse updateUser(
            int id,
            User user,
            String loggedInEmail) {

        User existingUser =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found with id : " + id
                                )
                        );

        // -------------------------------------------------
        // Ownership check
        // -------------------------------------------------

        validateUserOwnership(
                existingUser,
                loggedInEmail
        );

        // -------------------------------------------------
        // Update name
        // -------------------------------------------------

        if (user.getName() != null
                && !user.getName().trim().isEmpty()) {

            existingUser.setName(
                    user.getName().trim()
            );
        }

        // -------------------------------------------------
        // Update email
        // -------------------------------------------------

        if (user.getEmail() != null
                && !user.getEmail().trim().isEmpty()) {

            existingUser.setEmail(
                    user.getEmail().trim()
            );
        }

        // -------------------------------------------------
        // Update password
        // -------------------------------------------------

        if (user.getPassword() != null
                && !user.getPassword().trim().isEmpty()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
        }

        // -------------------------------------------------
        // IMPORTANT:
        //
        // Role cannot be changed by the user.
        // -------------------------------------------------

        User savedUser =
                userRepository.save(existingUser);

        return new UserResponse(savedUser);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    public LoginResponse loginUser(
            String email,
            String password) {

        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }

        User user =
                userRepository.findByEmail(email.trim());

        if (user == null) {
            return null;
        }

        // -------------------------------------------------
        // Verify BCrypt password
        // -------------------------------------------------

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            return null;
        }

        // -------------------------------------------------
        // Generate JWT
        // -------------------------------------------------

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        // -------------------------------------------------
        // Return safe user data
        // -------------------------------------------------

        return new LoginResponse(
                token,
                new UserResponse(user)
        );
    }

    // =====================================================
    // USER OWNERSHIP CHECK
    // =====================================================

    private void validateUserOwnership(
            User user,
            String loggedInEmail) {

        if (loggedInEmail == null
                || user.getEmail() == null
                || !user.getEmail().equalsIgnoreCase(
                        loggedInEmail)) {

            throw new SecurityException(
                    "You are not authorized to access this user account"
            );
        }
    }
}
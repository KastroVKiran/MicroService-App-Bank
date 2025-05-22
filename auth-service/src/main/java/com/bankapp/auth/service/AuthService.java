package com.bankapp.auth.service;

import com.bankapp.auth.model.User;
import com.bankapp.auth.repository.UserRepository;
import com.bankapp.auth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${user.service.url}")
    private String userServiceUrl;
    
    public Map<String, Object> registerUser(User user) {
        // Check if username is already taken
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        
        // Check if email is already in use
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        // Create new user
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setRoles(Collections.singleton("ROLE_USER"));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setEnabled(true);
        
        User savedUser = userRepository.save(newUser);
        
        // Create user profile in User Service
        createUserProfile(savedUser);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "User registered successfully");
        
        return result;
    }
    
    public Map<String, Object> loginUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        
        // Update last login time
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwt);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("roles", user.getRoles());
        
        return result;
    }
    
    private void createUserProfile(User user) {
        try {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("userId", user.getId());
            userProfile.put("email", user.getEmail());
            userProfile.put("firstName", user.getFirstName());
            userProfile.put("lastName", user.getLastName());
            
            restTemplate.postForObject(userServiceUrl + "/api/users", userProfile, Object.class);
        } catch (Exception e) {
            // Log the error but don't fail the registration
            System.err.println("Failed to create user profile: " + e.getMessage());
        }
    }
}
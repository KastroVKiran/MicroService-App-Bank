package com.bankapp.user.service;

import com.bankapp.user.model.UserProfile;
import com.bankapp.user.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }
    
    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found with id: " + id));
    }
    
    public UserProfile getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for userId: " + userId));
    }
    
    public UserProfile createUserProfile(UserProfile userProfile) {
        if (userProfileRepository.existsByUserId(userProfile.getUserId())) {
            throw new RuntimeException("User profile already exists for userId: " + userProfile.getUserId());
        }
        
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(userProfile);
    }
    
    public UserProfile updateUserProfile(Long userId, UserProfile userProfileDetails) {
        UserProfile userProfile = getUserProfileByUserId(userId);
        
        if (userProfileDetails.getFirstName() != null) {
            userProfile.setFirstName(userProfileDetails.getFirstName());
        }
        
        if (userProfileDetails.getLastName() != null) {
            userProfile.setLastName(userProfileDetails.getLastName());
        }
        
        if (userProfileDetails.getPhoneNumber() != null) {
            userProfile.setPhoneNumber(userProfileDetails.getPhoneNumber());
        }
        
        if (userProfileDetails.getDateOfBirth() != null) {
            userProfile.setDateOfBirth(userProfileDetails.getDateOfBirth());
        }
        
        if (userProfileDetails.getAddress() != null) {
            userProfile.setAddress(userProfileDetails.getAddress());
        }
        
        if (userProfileDetails.getOccupation() != null) {
            userProfile.setOccupation(userProfileDetails.getOccupation());
        }
        
        if (userProfileDetails.getEmployerName() != null) {
            userProfile.setEmployerName(userProfileDetails.getEmployerName());
        }
        
        userProfile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(userProfile);
    }
    
    public void deleteUserProfile(Long userId) {
        UserProfile userProfile = getUserProfileByUserId(userId);
        userProfileRepository.delete(userProfile);
    }
}
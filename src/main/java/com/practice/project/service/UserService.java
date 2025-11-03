package com.practice.project.service;

import com.practice.project.dto.UserRegistrationDTO;
import com.practice.project.dto.UserResponseDTO;
import com.practice.project.dto.UserUpdateDTO;
import com.practice.project.exception.UserNotFoundException;
import com.practice.project.model.User;
import com.practice.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO mapToResponseDTO(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRegisteredDate(user.getRegisteredDate());
        return dto;
    }

    public List<UserResponseDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO dto = mapToResponseDTO(user);
            userResponseDTOs.add(dto);
        }
        return userResponseDTOs;
    }

    public UserResponseDTO registerNewUser(UserRegistrationDTO dto){
        if (userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email is already in use");
        }

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setPhoneNumber(dto.getPhoneNumber());
        newUser.setRegisteredDate(LocalDateTime.now());

        User saveduser = userRepository.save(newUser);
        return mapToResponseDTO(saveduser);

    }

    public UserResponseDTO updateUserDetails(Long id, UserUpdateDTO dto) throws UserNotFoundException {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID"));
        userToUpdate.setName(dto.getName());
        userToUpdate.setEmail(dto.getEmail());
        userToUpdate.setPhoneNumber(dto.getPhoneNumber());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()){
            userToUpdate.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        User updatedUser = userRepository.save(userToUpdate);
        return mapToResponseDTO(updatedUser);
    }

    public UserResponseDTO findUserResponseById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID"));
        return mapToResponseDTO(user);
    }

    public void deleteUserById(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found with ID");
        }
        userRepository.deleteById(id);
    }

    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }

}

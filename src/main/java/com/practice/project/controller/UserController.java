package com.practice.project.controller;

import com.practice.project.dto.UserRegistrationDTO;
import com.practice.project.dto.UserResponseDTO;
import com.practice.project.dto.UserUpdateDTO;
import com.practice.project.exception.UserNotFoundException;
import com.practice.project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){
        try{
            UserResponseDTO responseDTO = userService.findUserResponseById(id);
            return ResponseEntity.ok(responseDTO);
        }catch (UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO dto){
        UserResponseDTO responseDTO = userService.registerNewUser(dto);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto){
        try{
            UserResponseDTO responseDTO = userService.updateUserDetails(id, dto);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 : NOT FOUND
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        try{
            userService.deleteUserById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}

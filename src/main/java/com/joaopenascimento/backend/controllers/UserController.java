package com.joaopenascimento.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joaopenascimento.backend.dto.property.PropertyDTO;
import com.joaopenascimento.backend.dto.user.UserCreateDTO;
import com.joaopenascimento.backend.dto.user.UserDTO;
import com.joaopenascimento.backend.dto.user.UserUpdateDTO;
import com.joaopenascimento.backend.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserDTO> getMe() {
        
        UserDTO user = userService.getMe();
        
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> update(@RequestBody UserUpdateDTO dto) {

        UserDTO updatedUser = userService.update(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCreateDTO dto) {
        
        UserDTO newUser = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<PropertyDTO>> getFavorites() {

        List<PropertyDTO> favorites = userService.getFavorites();

        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/favorites/{propertyId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long propertyId) {

        userService.addFavorite(propertyId);
    
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/favorites/{propertyId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long propertyId) {

        userService.removeFavorite(propertyId);

        return ResponseEntity.noContent().build();
    }
}

package com.joaopenascimento.backend.services;

import com.joaopenascimento.backend.dto.auth.RegisterDTO;
import com.joaopenascimento.backend.dto.property.PropertyDTO;
import com.joaopenascimento.backend.dto.user.UserCreateDTO;
import com.joaopenascimento.backend.dto.user.UserDTO;
import com.joaopenascimento.backend.dto.user.UserUpdateDTO;
import com.joaopenascimento.backend.model.Property;
import com.joaopenascimento.backend.model.User;
import com.joaopenascimento.backend.model.enums.UserRole;
import com.joaopenascimento.backend.repositories.PropertyRepository;
import com.joaopenascimento.backend.repositories.UserRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PropertyRepository propertyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {
        
        User user = getAuthenticatedUser();

        return new UserDTO(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO create(UserCreateDTO dto) {
        
        User currentUser = getAuthenticatedUser();
        
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Você não tem permissão para criar usuários.");
        }
        
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Este email já está em uso.");
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setRole(dto.role());
        user.setPassword(passwordEncoder.encode(dto.password()));

        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(UserUpdateDTO dto) {
        
        User user = getAuthenticatedUser();

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if(dto.password() != null) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> getFavorites() {
        
        User user = getAuthenticatedUser();

        return user.getFavorites().stream()
                .map(PropertyDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFavorite(Long propertyId) {

        User user = getAuthenticatedUser();

        Property property = propertyRepository.findById(propertyId)
                                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        if (user.getFavorites().contains(property)) {
            throw new RuntimeException("Imóvel já está nos favoritos");
        }

        user.getFavorites().add(property);

        userRepository.save(user);
    }

    @Transactional
    public void removeFavorite(Long propertyId) {

        User user = getAuthenticatedUser();

        Property property = propertyRepository.findById(propertyId)
                                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        user.getFavorites().remove(property);
    }

    @Transactional
    public void register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Este email já está em uso");
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(UserRole.CLIENTE);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
    }
}

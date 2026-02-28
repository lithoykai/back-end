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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthenticatedUser(User user) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(user.getEmail());
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Deve criar usuário se for admin")
    void create_Success() {
        User admin = new User(1L, "admin@test.com", "pass", "Admin", UserRole.ADMIN, new HashSet<>());
        mockAuthenticatedUser(admin);

        UserCreateDTO dto = new UserCreateDTO("New User", "new@test.com", "123456", UserRole.CORRETOR);

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDTO result = userService.create(dto);

        assertNotNull(result);
        assertEquals(dto.email(), result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário se não for admin")
    void create_Forbidden() {
        User commonUser = new User(2L, "user@test.com", "pass", "User", UserRole.CLIENTE, new HashSet<>());
        mockAuthenticatedUser(commonUser);

        UserCreateDTO dto = new UserCreateDTO("New User", "new@test.com", "123456", UserRole.CORRETOR);

        assertThrows(RuntimeException.class, () -> userService.create(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar nome e senha")
    void update_Success() {
        User user = new User(1L, "user@test.com", "oldPass", "Old Name", UserRole.CLIENTE, new HashSet<>());
        mockAuthenticatedUser(user);

        UserUpdateDTO dto = new UserUpdateDTO("New Name", "newPass");

        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDTO result = userService.update(dto);

        assertEquals("New Name", result.name());
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    @DisplayName("Deve adicionar imóvel aos favoritos")
    void addFavorite_Success() {
        User user = new User(1L, "user@test.com", "pass", "User", UserRole.CLIENTE, new HashSet<>());
        mockAuthenticatedUser(user);

        Property property = new Property();
        property.setId(10L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));

        userService.addFavorite(10L);

        assertTrue(user.getFavorites().contains(property));
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar duplicado aos favoritos")
    void addFavorite_Duplicate() {
        User user = new User(1L, "user@test.com", "pass", "User", UserRole.CLIENTE, new HashSet<>());
        Property property = new Property();
        property.setId(10L);
        user.getFavorites().add(property);
        
        mockAuthenticatedUser(user);
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));

        assertThrows(RuntimeException.class, () -> userService.addFavorite(10L));
    }

    @Test
    @DisplayName("Deve registrar um novo usuário")
    void register_Success() {
        RegisterDTO dto = new RegisterDTO("New Client", "client@test.com", "123456");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded");
        
        userService.register(dto);

        verify(userRepository).save(argThat(u -> 
            u.getEmail().equals(dto.email()) && u.getRole() == UserRole.CLIENTE
        ));
    }
    
    @Test
    @DisplayName("Deve retornar a lista de favoritos")
    void getFavorites_Success() {
        User user = new User();
        user.setEmail("test@test.com");
        Property p1 = new Property(); p1.setId(1L); p1.setName("Prop1"); p1.setBroker(new User());
        user.setFavorites(Set.of(p1));
        
        mockAuthenticatedUser(user);
        
        List<PropertyDTO> favorites = userService.getFavorites();
        
        assertFalse(favorites.isEmpty());
        assertEquals(1, favorites.size());
    }
}
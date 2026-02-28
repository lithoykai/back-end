package com.joaopenascimento.backend.services;

import com.joaopenascimento.backend.dto.property.PropertyCreateDTO;
import com.joaopenascimento.backend.dto.property.PropertyDTO;
import com.joaopenascimento.backend.dto.property.PropertyUpdateDTO;
import com.joaopenascimento.backend.model.Property;
import com.joaopenascimento.backend.model.User;
import com.joaopenascimento.backend.model.enums.PropertyType;
import com.joaopenascimento.backend.model.enums.UserRole;
import com.joaopenascimento.backend.repositories.PropertyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @InjectMocks
    private PropertyService propertyService;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Deve criar um imóvel se o usuário for corretor")
    void create_Success() {
        User corretor = new User();
        corretor.setId(1L);
        corretor.setRole(UserRole.CORRETOR);
        corretor.setName("corretor");

        when(userService.getAuthenticatedUser()).thenReturn(corretor);
        when(propertyRepository.save(any(Property.class))).thenAnswer(i -> {
            Property p = i.getArgument(0);
            p.setId(100L);
            return p;
        });

        PropertyCreateDTO dto = new PropertyCreateDTO(
            "Casa", "Descrição", PropertyType.CASA, 500.0, 50, 2, "rua 1", "Recife", "PE", null
        );

        PropertyDTO result = propertyService.create(dto);

        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(corretor.getId(), result.brokerId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando um cliente tenta criar um imóvel")
    void create_Forbidden() {
        User cliente = new User();
        cliente.setRole(UserRole.CLIENTE);

        when(userService.getAuthenticatedUser()).thenReturn(cliente);

        PropertyCreateDTO dto = new PropertyCreateDTO(
            "Casa", "Descrição", PropertyType.CASA, 500.0, 50, 2, "rua 1", "Recife", "PE", null
        );

        assertThrows(RuntimeException.class, () -> propertyService.create(dto));
        verify(propertyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o imóvel se o usuário for o dono")
    void update_Success_Owner() {
        User owner = new User(); 
        owner.setId(1L); 
        owner.setRole(UserRole.CORRETOR); 
        owner.setName("Owner");
        
        Property property = new Property();
        property.setId(10L);
        property.setBroker(owner);
        property.setActive(true);

        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        PropertyUpdateDTO dto = new PropertyUpdateDTO(
            "Casa nova", null, null, 900.0, null, null, null, null, null, null
        );

        PropertyDTO result = propertyService.update(10L, dto);

        assertEquals("Casa nova", result.name());
        assertEquals(900.0, result.value());
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário tentar atualizar o imóvel e não for o dono ou admin")
    void update_Forbidden() {
        User owner = new User(); 
        owner.setId(1L);
        
        User intruder = new User(); 
        intruder.setId(2L); 
        intruder.setRole(UserRole.CORRETOR);

        Property property = new Property();
        property.setId(10L);
        property.setBroker(owner);

        when(userService.getAuthenticatedUser()).thenReturn(intruder);
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));

        PropertyUpdateDTO dto = new PropertyUpdateDTO("Casa", null, null, null, null, null, null, null, null, null);

        assertThrows(RuntimeException.class, () -> propertyService.update(10L, dto));
        verify(propertyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar o imóvel")
    void delete_Success_Admin() {
        User admin = new User(); 
        admin.setId(99L); 
        admin.setRole(UserRole.ADMIN);
        
        User corretor = new User(); 
        corretor.setId(1L);

        Property property = new Property();
        property.setId(10L);
        property.setBroker(corretor);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userService.getAuthenticatedUser()).thenReturn(admin);

        propertyService.delete(10L);

        verify(propertyRepository).delete(property);
    }
    
    @Test
    @DisplayName("Deve alterar o status")
    void toggleStatus_Success() {
        User owner = new User(); 
        owner.setId(1L); 
        owner.setRole(UserRole.CORRETOR); 
        owner.setName("Test");
        
        Property property = new Property();
        property.setId(10L);
        property.setBroker(owner);
        property.setActive(true);
        
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        
        PropertyDTO result = propertyService.toggleStatus(10L);
        
        assertFalse(result.active());
    }

    @Test
    @DisplayName("Deve retornar todos os imóveis")
    void findAll_Search() {
        Pageable pageable = Pageable.unpaged();
        Property p = new Property(); p.setBroker(new User());
        Page<Property> page = new PageImpl<>(List.of(p));

        when(propertyRepository.findAll(ArgumentMatchers.<Specification<Property>>any(), eq(pageable)))
        .thenReturn(page);

        var result = propertyService.findAll("test", null, null, null, null, pageable);

        assertFalse(result.isEmpty());
        verify(propertyRepository).findAll(ArgumentMatchers.<Specification<Property>>any(), eq(pageable));
    }
}
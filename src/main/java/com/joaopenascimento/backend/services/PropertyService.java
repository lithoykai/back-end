package com.joaopenascimento.backend.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.joaopenascimento.backend.dto.property.PropertyCreateDTO;
import com.joaopenascimento.backend.dto.property.PropertyDTO;
import com.joaopenascimento.backend.dto.property.PropertyUpdateDTO;
import com.joaopenascimento.backend.model.Property;
import com.joaopenascimento.backend.model.User;
import com.joaopenascimento.backend.model.enums.PropertyType;
import com.joaopenascimento.backend.model.enums.UserRole;
import com.joaopenascimento.backend.repositories.PropertyRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyService {
    
    private final PropertyRepository propertyRepository;

    private final UserService userService;

    public  PropertyService(PropertyRepository propertyRepository, UserService userService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Page<PropertyDTO> findAll(
        String name,
        PropertyType type,
        Double minPrice,
        Double maxPrice,
        Integer minBedrooms,
        Pageable pageable
    ) {
        Specification<Property> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> 
                    cb.equal(root.get("type"), type));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("value"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("value"), maxPrice));
        }

        if (minBedrooms != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms));
        }

        return propertyRepository.findAll(spec, pageable)
                .map(PropertyDTO::new);
    }

    @Transactional(readOnly = true)
    public PropertyDTO findPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        return new PropertyDTO(property);
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> findAllByBrokerId() {
        
        User currentUser = userService.getAuthenticatedUser();

        List<Property> properties = propertyRepository.findByBrokerId(currentUser.getId());

        return properties.stream()
                .map(PropertyDTO::new)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN', 'CORRETOR')")
    public PropertyDTO create(PropertyCreateDTO dto) {

        User currentUser = userService.getAuthenticatedUser();

        if (currentUser.getRole() == UserRole.CLIENTE) {
            throw new RuntimeException("Você não tem permissão para criar um imóvel");
        }
        
        Property property = new Property();

        property.setName(dto.name());
        property.setDescription(dto.description());
        property.setValue(dto.value());
        property.setArea(dto.area());
        property.setBedrooms(dto.bedrooms());
        property.setAddress(dto.address());
        property.setCity(dto.city());
        property.setState(dto.state());
        property.setType(dto.type());
        property.setImageUrls(dto.imageUrls());
        property.setBroker(currentUser);

        propertyRepository.save(property);

        return new PropertyDTO(property);
    }

    @Transactional
    public PropertyDTO update(Long propertyId, PropertyUpdateDTO dto) {
        Property property = propertyRepository.findById(propertyId)
                                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        User currentUser = userService.getAuthenticatedUser();

        boolean isAdmin = currentUser.getRole().equals(UserRole.ADMIN);
        boolean isOwner = property.getBroker().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Acesso negado: Apenas o corretor responsável ou administradores podem alterar este imóvel.");
        }
        
        if (dto.name() != null) {property.setName(dto.name());}

        if (dto.description() != null) {property.setDescription(dto.description());}

        if (dto.value() != null) {property.setValue(dto.value());}

        if (dto.area() != null) {property.setArea(dto.area());}

        if (dto.address() != null) {property.setAddress(dto.address());}

        if (dto.city() != null) {property.setCity(dto.city());}

        if (dto.state() != null) {property.setState(dto.state());}

        if (dto.bedrooms() != null) property.setBedrooms(dto.bedrooms());

        if (dto.type() != null) property.setType(dto.type());

        property = propertyRepository.save(property);
        return new PropertyDTO(property);
    }

    @Transactional
    public void delete(Long propertyId) {

        Property property = propertyRepository.findById(propertyId)
                                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));
        
        User currentUser = userService.getAuthenticatedUser();

        if (currentUser.getRole() != UserRole.ADMIN && !property.getBroker().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Você não tem permissão para excluir este imóvel");
        }

        propertyRepository.delete(property);
    }
    
    @Transactional
    public PropertyDTO toggleStatus(Long propertyId) {

        Property property = propertyRepository.findById(propertyId)
                                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        User currentUser = userService.getAuthenticatedUser();

        boolean isAdmin = currentUser.getRole().equals(UserRole.ADMIN);
        boolean isOwner = property.getBroker().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Acesso negado: Apenas o corretor responsável ou administradores podem alterar este imóvel.");
        }

        property.setActive(!property.getActive());

        return new PropertyDTO(property);
    }
}

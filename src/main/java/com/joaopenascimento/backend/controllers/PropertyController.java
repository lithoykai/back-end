package com.joaopenascimento.backend.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joaopenascimento.backend.dto.property.PropertyCreateDTO;
import com.joaopenascimento.backend.dto.property.PropertyDTO;
import com.joaopenascimento.backend.dto.property.PropertyUpdateDTO;
import com.joaopenascimento.backend.model.enums.PropertyType;
import com.joaopenascimento.backend.services.PropertyService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/property")
public class PropertyController {
    
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService){
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<Page<PropertyDTO>> getAll(
        @RequestParam(required = false) String name,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minBedrooms,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PropertyDTO> page = propertyService.findAll(name, type, minPrice, maxPrice, minBedrooms, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        PropertyDTO property = propertyService.findPropertyById(id);

        return ResponseEntity.ok(property);
    }

    @GetMapping("/getUserProperties")
    public ResponseEntity<List<PropertyDTO>> getUserProperties() {
        List<PropertyDTO> list = propertyService.findAllByBrokerId();

        return ResponseEntity.ok(list);
    }
    
    @PostMapping
    public ResponseEntity<PropertyDTO> create(
        @RequestBody @Valid PropertyCreateDTO dto
    ) {
        PropertyDTO newProperty = propertyService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(newProperty);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PropertyUpdateDTO dto
    ) {
        PropertyDTO updatedProperty = propertyService.update(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedProperty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        propertyService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<PropertyDTO> toggleStatus(@PathVariable Long id) {
        
        PropertyDTO property = propertyService.toggleStatus(id);
        
        return ResponseEntity.ok(property);
    }
}

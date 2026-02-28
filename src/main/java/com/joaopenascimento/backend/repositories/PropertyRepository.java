package com.joaopenascimento.backend.repositories;

import com.joaopenascimento.backend.model.Property;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findByBrokerId(Long brokerId);
}

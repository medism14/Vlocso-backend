package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
}
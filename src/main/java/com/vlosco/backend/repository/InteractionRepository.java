package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.Interaction;

public interface InteractionRepository extends JpaRepository<  Interaction, Long>{

}

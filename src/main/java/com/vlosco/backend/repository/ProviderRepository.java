package com.vlosco.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.enums.ProviderNames;
import com.vlosco.backend.model.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    public Optional<Provider> findByProviderName(ProviderNames providerName);
}

package com.vlosco.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.RefreshTokensBlacklist;

public interface RefreshTokensBlacklistRepository extends JpaRepository<RefreshTokensBlacklist, Long> {

    public Optional<RefreshTokensBlacklist> findByRefreshToken(String refreshToken);

}

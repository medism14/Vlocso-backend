package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import com.vlosco.backend.model.Interaction;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    @Query("SELECT i FROM Interaction i WHERE i.user.id = :userId")
    List<Interaction> findByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Interaction i WHERE i.user.id = :userId AND i.interactionType = 'Search'")
    Optional<List<Interaction>> findByUserSearchs(@Param("userId") Long userId);
}

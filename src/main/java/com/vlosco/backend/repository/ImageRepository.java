package com.vlosco.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vlosco.backend.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query(value = "SELECT * FROM images WHERE annonce_id = :annonceId", nativeQuery = true)
    List<Image> findByAnnonceId(@Param("annonceId") Long annonceId);
}

package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.GameMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameMasterRepository extends JpaRepository<GameMaster, UUID>
{
    Optional<GameMaster> findByNome(String nome);
}

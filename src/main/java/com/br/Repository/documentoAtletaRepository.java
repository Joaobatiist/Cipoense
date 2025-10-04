package com.br.Repository;

import com.br.Entity.documentoAtleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface documentoAtletaRepository extends JpaRepository<documentoAtleta, UUID> {

    // Encontrar documentos por ID do atleta
    List<documentoAtleta> findByAtletaId(UUID atletaId);

    // Encontrar um documento específico por ID e ID do atleta
    Optional<documentoAtleta> findByIdAndAtletaId(UUID id, UUID atletaId);

    // Contar documentos de um atleta
    long countByAtletaId(UUID atletaId);
}

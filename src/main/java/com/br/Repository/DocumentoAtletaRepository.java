package com.br.Repository;

import com.br.Entity.DocumentoAtleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoAtletaRepository extends JpaRepository<DocumentoAtleta, Long> {

    // Encontrar documentos por ID do atleta
    List<DocumentoAtleta> findByAtletaId(Long atletaId);

    // Encontrar um documento específico por ID e ID do atleta
    Optional<DocumentoAtleta> findByIdAndAtletaId(Long id, Long atletaId);

    // Contar documentos de um atleta
    long countByAtletaId(Long atletaId);
}

package com.br.Repository;

import com.br.Entity.documentoAtleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface documentoAtletaRepository extends JpaRepository<documentoAtleta, Long> {

    // Encontrar documentos por ID do atleta
    List<documentoAtleta> findByAtletaId(Long atletaId);

    // Encontrar um documento específico por ID e ID do atleta
    Optional<documentoAtleta> findByIdAndAtletaId(Long id, Long atletaId);

    // Contar documentos de um atleta
    long countByAtletaId(Long atletaId);
}

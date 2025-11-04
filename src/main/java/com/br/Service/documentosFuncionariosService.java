package com.br.Service;

import com.br.Entity.documentosFuncionarios;
import com.br.Repository.documentosFuncionariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class documentosFuncionariosService {

    private final documentosFuncionariosRepository documentosFuncionariosRepository;

    public documentosFuncionariosService (documentosFuncionariosRepository documentosFuncionariosRepository) {
        // Removido o @Autowired redundante
        this.documentosFuncionariosRepository = documentosFuncionariosRepository;
    }

    @Transactional
    public documentosFuncionarios cadastroDocumentosFuncionarios(documentosFuncionarios documentosFuncionarios) {
        return documentosFuncionariosRepository.save(documentosFuncionarios);
    }

    // Lógica CORRIGIDA: Se ID presente, busca um. Se ID nulo, busca todos.
    @Transactional
    public List<documentosFuncionarios> exibirDocumentosFuncionarios(UUID id) {
        if (id != null) {
            documentosFuncionarios documento = documentosFuncionariosRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Documento não encontrado com ID: " + id));

            // Retorna o documento encontrado dentro de uma lista (para manter a assinatura List<...>)
            List<documentosFuncionarios> result = new ArrayList<>();
            result.add(documento);
            return result;
        }

        // Se o ID for null, retorna todos
        return documentosFuncionariosRepository.findAll();
    }

    @Transactional
    public documentosFuncionarios atualizarDocumentosFuncionarios(documentosFuncionarios documentosAtualizado) {
        documentosFuncionarios existDocumentos = documentosFuncionariosRepository.findById(documentosAtualizado.getId())
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com ID: " + documentosAtualizado.getId()));

        // Atualizar campos existentes
        if (documentosAtualizado.getDescricao() != null) {
            existDocumentos.setDescricao(documentosAtualizado.getDescricao());
        }
        if (documentosAtualizado.getDataUpload() != null) {
            existDocumentos.setDataUpload(documentosAtualizado.getDataUpload());
        }
        if (documentosAtualizado.getNomeArquivo() != null) {
            existDocumentos.setNomeArquivo(documentosAtualizado.getNomeArquivo());
        }

        // ✅ ADICIONAR ESTA PARTE - MUITO IMPORTANTE!
        if (documentosAtualizado.getDocumentos() != null) {
            existDocumentos.setDocumentos(documentosAtualizado.getDocumentos());
            // Opcionalmente, atualizar a data quando o arquivo muda
            existDocumentos.setDataUpload(LocalDateTime.now());
        }

        return documentosFuncionariosRepository.save(existDocumentos);
    }

    @Transactional
    public void deletarDocumentosFuncionarios(UUID id) {
        if (!documentosFuncionariosRepository.existsById(id)) {
            throw new RuntimeException("Documento não encontrado com ID: " + id);
        }
        documentosFuncionariosRepository.deleteById(id);
    }
}
package com.br.Service;

import com.br.Entity.atleta;
import com.br.Entity.documentoAtleta;
import com.br.Entity.responsavel;
import com.br.Repository.atletaRepository;
import com.br.Repository.documentoAtletaRepository;
import com.br.Repository.presencaRepository;
import com.br.Repository.responsavelRepository;
import com.br.Response.atletaProfileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class atletaSupervisorService {

    private final atletaRepository atletaRepository;
    private final documentoAtletaRepository documentoRepository;
    private final responsavelRepository responsavelRepository;
    private final presencaRepository presencaRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public atletaSupervisorService(
            atletaRepository atletaRepository,
            documentoAtletaRepository documentoRepository,
            responsavelRepository responsavelRepository,
            presencaRepository presencaRepository
    ) {
        this.atletaRepository = atletaRepository;
        this.documentoRepository = documentoRepository;
        this.responsavelRepository = responsavelRepository;
        this.presencaRepository = presencaRepository;
    }

    // --- CRUD para Atletas pelo Supervisor ---

    public List<atletaProfileDto> getAllAtletas() {
        return atletaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public atletaProfileDto getAtletaProfile(UUID atletaId) {
        atleta atleta = findAtletaById(atletaId);
        return convertToDto(atleta);
    }

    public atletaProfileDto updateAtleta(UUID atletaId, atletaProfileDto profileDto) {
        atleta atleta = findAtletaById(atletaId);

        // Atualiza campos com base no DTO
        atleta.setNome(profileDto.getNome());
        atleta.setEmail(profileDto.getEmail());
        atleta.setSubDivisao(profileDto.getSubDivisao());
        atleta.setIsAptoParaJogar(profileDto.getIsAptoParaJogar()); // Atualiza aptidão
        atleta.setPosicao(profileDto.getPosicao());

        // Data de Nascimento
        if (profileDto.getDataNascimento() != null && !profileDto.getDataNascimento().trim().isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(profileDto.getDataNascimento(), DATE_FORMATTER);
                atleta.setDataNascimento(parsedDate);
            } catch (DateTimeParseException e) {
                try {
                    LocalDate parsedDate = LocalDate.parse(profileDto.getDataNascimento());
                    atleta.setDataNascimento(parsedDate);
                } catch (DateTimeParseException ex) {
                    throw new RuntimeException("Formato de data de nascimento inválido. Use DD/MM/YYYY ou YYYY-MM-DD.", ex);
                }
            }
        } else {
            atleta.setDataNascimento(null);
        }

        // Contato Responsável
        if (profileDto.getContatoResponsavel() != null && !profileDto.getContatoResponsavel().trim().isEmpty()) {
            responsavel responsavel = atleta.getResponsavel();
            if (responsavel == null) {
                responsavel = new responsavel();
                atleta.setResponsavel(responsavel);
            }
            responsavel.setTelefone(profileDto.getContatoResponsavel());
            responsavelRepository.save(responsavel);
        } else {
            if (atleta.getResponsavel() != null) {
                atleta.getResponsavel().setTelefone(null);
                responsavelRepository.save(atleta.getResponsavel());
            }
        }

        // CORREÇÃO: Se o PDF for enviado junto com a atualização do perfil (opcional, mas possível)
        if (profileDto.getDocumentoPdfBase64() != null && !profileDto.getDocumentoPdfBase64().isEmpty()) {
            try {
                String base64Data = profileDto.getDocumentoPdfBase64();
                // Remove o prefixo do Data URI se presente
                if (base64Data.contains(",")) {
                    base64Data = base64Data.split(",")[1];
                }
                // ALTERAÇÃO: Como documentoPdfBytes agora é String, salvamos o base64 diretamente
                atleta.setDocumentoPdfBytes(base64Data);
                atleta.setDocumentoPdfContentType(profileDto.getDocumentoPdfContentType());
            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar Base64 do PDF: " + e.getMessage(), e);
            }
        }

        atleta updatedAtleta = atletaRepository.save(atleta);
        return convertToDto(updatedAtleta);
    }

    public void deleteAtleta(UUID atletaId) {
        atleta atleta = findAtletaById(atletaId);
        atletaRepository.delete(atleta);
        presencaRepository.deleteByAtleta(atleta);
    }

    // --- Gerenciamento de Documentos PDF (principal) ---

    // CORREÇÃO: Este método agora recebe um MultipartFile e salva como String Base64 no DB
    public String uploadDocumentoPdf(UUID atletaId, MultipartFile file) {
        atleta atleta = findAtletaById(atletaId);

        try {
            byte[] bytes = file.getBytes();
            String base64String = Base64.getEncoder().encodeToString(bytes);

            // ALTERAÇÃO: Como documentoPdfBytes agora é String, salvamos o base64 diretamente
            atleta.setDocumentoPdfBytes(base64String);
            atleta.setDocumentoPdfContentType(file.getContentType());
            atletaRepository.save(atleta);

            // Retorna a URL base64 para o frontend exibir
            return "data:" + file.getContentType() + ";base64," + base64String;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o documento PDF no banco de dados", e);
        }
    }

    // Método para remover o PDF principal
    public void deleteMainPdf(UUID atletaId) {
        atleta atleta = findAtletaById(atletaId);
        atleta.setDocumentoPdfBytes(null);
        atleta.setDocumentoPdfContentType(null);
        atletaRepository.save(atleta);
    }

    private atleta findAtletaById(UUID atletaId) {
        return atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));
    }

    // CORREÇÃO: Converte Entidade Atleta para DTO AtletaProfileDto
    private atletaProfileDto convertToDto(atleta atleta) {
        atletaProfileDto dto = new atletaProfileDto();
        dto.setId(atleta.getId());
        dto.setMatricula(atleta.getMatricula());
        dto.setNome(atleta.getNome());
        dto.setEmail(atleta.getEmail());
        dto.setSubDivisao(atleta.getSubDivisao());
        dto.setPosicao(atleta.getPosicao());
        dto.setIsAptoParaJogar(atleta.getIsAptoParaJogar());

        dto.setDataNascimento(atleta.getDataNascimento() != null ? atleta.getDataNascimento().format(DATE_FORMATTER) : null);

        // CORREÇÃO: Foto agora é String (Base64)
        if (atleta.getFoto() != null && !atleta.getFoto().isEmpty() && atleta.getFotoContentType() != null) {
            dto.setFoto("data:" + atleta.getFotoContentType() + ";base64," + atleta.getFoto());
        } else {
            dto.setFoto(null);
        }

        // Contato Responsável
        if (atleta.getResponsavel() != null) {
            dto.setContatoResponsavel(atleta.getResponsavel().getTelefone());
        } else {
            dto.setContatoResponsavel(null);
        }

        // CORREÇÃO: PDF agora é String (Base64)
        if (atleta.getDocumentoPdfBytes() != null && !atleta.getDocumentoPdfBytes().isEmpty() && atleta.getDocumentoPdfContentType() != null) {
            dto.setDocumentoPdfBase64(atleta.getDocumentoPdfBytes());
            dto.setDocumentoPdfContentType(atleta.getDocumentoPdfContentType());
        } else {
            dto.setDocumentoPdfBase64(null);
            dto.setDocumentoPdfContentType(null);
        }

        // Lista de outros documentos (se você ainda usa a entidade DocumentoAtleta)
        if (atleta.getDocumentos() != null && !atleta.getDocumentos().isEmpty()) {
            dto.setDocumentos(atleta.getDocumentos().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setDocumentos(List.of());
        }

        return dto;
    }

    // Método para converter DocumentoAtleta para DocumentoDto (se ainda usado)
    private atletaProfileDto.DocumentoDto convertToDto(documentoAtleta documento) {
        atletaProfileDto.DocumentoDto dto = new atletaProfileDto.DocumentoDto();
        dto.setId(documento.getId());
        dto.setNome(documento.getNome());
        dto.setUrl(documento.getUrl()); // Esta URL ainda existiria se outros documentos forem salvos no disco
        dto.setTipo(documento.getTipo());
        return dto;
    }
}
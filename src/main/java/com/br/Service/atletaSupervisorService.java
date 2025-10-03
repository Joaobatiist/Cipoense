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
import java.util.stream.Collectors;

@Service
public class atletaSupervisorService {

    private final atletaRepository atletaRepository;
    private final documentoAtletaRepository documentoRepository;
    private final responsavelRepository responsavelRepository;
    private final presencaRepository presencaRepository;

    // O uploadDir não é mais necessário para o PDF principal, mas pode ser para outros documentos
    // private final String uploadDir;

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

    public atletaProfileDto getAtletaProfile(Long atletaId) {
        atleta atleta = findAtletaById(atletaId);
        return convertToDto(atleta);
    }

    public atletaProfileDto updateAtleta(Long atletaId, atletaProfileDto profileDto) {
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

        // Se o PDF for enviado junto com a atualização do perfil (opcional, mas possível)
        // Isso depende de como o frontend enviará os dados. Se for um campo separado, use o método uploadDocumentoPdf.
        if (profileDto.getDocumentoPdfBase64() != null && !profileDto.getDocumentoPdfBase64().isEmpty()) {
            try {
                String base64Data = profileDto.getDocumentoPdfBase64();
                // Remove o prefixo do Data URI se presente
                if (base64Data.contains(",")) {
                    base64Data = base64Data.split(",")[1];
                }
                atleta.setDocumentoPdfBytes(Base64.getDecoder().decode(base64Data));
                atleta.setDocumentoPdfContentType(profileDto.getDocumentoPdfContentType());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Erro ao decodificar Base64 do PDF: " + e.getMessage(), e);
            }
        }



        atleta updatedAtleta = atletaRepository.save(atleta);
        return convertToDto(updatedAtleta);
    }

    public void deleteAtleta(Long atletaId) {
        atleta atleta = findAtletaById(atletaId);
        atletaRepository.delete(atleta);
        presencaRepository.deleteByAtleta(atleta);
    }

    // --- Gerenciamento de Documentos PDF (principal) ---

    // Este método agora recebe um MultipartFile e salva seus bytes no DB
    public String uploadDocumentoPdf(Long atletaId, MultipartFile file) {
        atleta atleta = findAtletaById(atletaId);

        try {
            atleta.setDocumentoPdfBytes(file.getBytes());
            atleta.setDocumentoPdfContentType(file.getContentType());
            atletaRepository.save(atleta);
            // Retorna a URL base64 para o frontend exibir
            return "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o documento PDF no banco de dados", e);
        }
    }

    // Método para remover o PDF principal
    public void deleteMainPdf(Long atletaId) {
        atleta atleta = findAtletaById(atletaId);
        atleta.setDocumentoPdfBytes(null);
        atleta.setDocumentoPdfContentType(null);
        atletaRepository.save(atleta);
    }

    // Se ainda precisar de upload/delete para outros documentos (DocumentoAtleta),
    // mantenha os métodos `uploadDocuments` e `deleteAtletaDocument` e suas dependências com `uploadDir`.
    // Por exemplo, `saveDocument` precisaria do `uploadDir`.
    // Se o PDF principal for o único documento, remova a classe `DocumentoAtleta` e todos os métodos relacionados a ela.

    // --- Métodos Auxiliares ---

    private atleta findAtletaById(Long atletaId) {
        return atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));
    }

    // Converte Entidade Atleta para DTO AtletaProfileDto
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

        // Foto (Base64)
        if (atleta.getFoto() != null && atleta.getFoto().length > 0 && atleta.getFotoContentType() != null) {
            dto.setFoto("data:" + atleta.getFotoContentType() + ";base64," + Base64.getEncoder().encodeToString(atleta.getFoto()));
        } else {
            dto.setFoto(null);
        }

        // Contato Responsável
        if (atleta.getResponsavel() != null) {
            dto.setContatoResponsavel(atleta.getResponsavel().getTelefone());
        } else {
            dto.setContatoResponsavel(null);
        }

        // --- CONVERTE BYTES DO PDF PARA BASE64 PARA O FRONTEND ---
        if (atleta.getDocumentoPdfBytes() != null && atleta.getDocumentoPdfBytes().length > 0 && atleta.getDocumentoPdfContentType() != null) {
            dto.setDocumentoPdfBase64(Base64.getEncoder().encodeToString(atleta.getDocumentoPdfBytes()));
            dto.setDocumentoPdfContentType(atleta.getDocumentoPdfContentType());
        } else {
            dto.setDocumentoPdfBase64(null);
            dto.setDocumentoPdfContentType(null);
        }
        // --- FIM DA CONVERSÃO DE BYTES DO PDF ---

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
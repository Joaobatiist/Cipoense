package com.br.Service;

import com.br.Entity.Atleta;
import com.br.Entity.DocumentoAtleta;
import com.br.Entity.Responsavel;
import com.br.Repository.AtletaRepository;
import com.br.Repository.DocumentoAtletaRepository;
import com.br.Repository.ResponsavelRepository;
import com.br.Response.AtletaProfileDto;
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
public class AtletaSupervisorService {

    private final AtletaRepository atletaRepository;
    private final DocumentoAtletaRepository documentoRepository;
    private final ResponsavelRepository responsavelRepository;

    // O uploadDir não é mais necessário para o PDF principal, mas pode ser para outros documentos
    // private final String uploadDir;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AtletaSupervisorService(
            AtletaRepository atletaRepository,
            DocumentoAtletaRepository documentoRepository,
            ResponsavelRepository responsavelRepository
            // @Value("${file.upload-dir}") String uploadDir // Remover se não for mais usado
    ) {
        this.atletaRepository = atletaRepository;
        this.documentoRepository = documentoRepository;
        this.responsavelRepository = responsavelRepository;
        // this.uploadDir = uploadDir; // Remover se não for mais usado
    }

    // --- CRUD para Atletas pelo Supervisor ---

    public List<AtletaProfileDto> getAllAtletas() {
        return atletaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AtletaProfileDto getAtletaProfile(Long atletaId) {
        Atleta atleta = findAtletaById(atletaId);
        return convertToDto(atleta);
    }

    public AtletaProfileDto updateAtleta(Long atletaId, AtletaProfileDto profileDto) {
        Atleta atleta = findAtletaById(atletaId);

        // Atualiza campos com base no DTO
        atleta.setNome(profileDto.getNome());
        atleta.setEmail(profileDto.getEmail());
        atleta.setSubDivisao(profileDto.getSubDivisao());
        atleta.setIsAptoParaJogar(profileDto.getIsAptoParaJogar()); // Atualiza aptidão

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
            Responsavel responsavel = atleta.getResponsavel();
            if (responsavel == null) {
                responsavel = new Responsavel();
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
                atleta.setDocumentoPdfBytes(Base64.getDecoder().decode(profileDto.getDocumentoPdfBase64()));
                atleta.setDocumentoPdfContentType(profileDto.getDocumentoPdfContentType());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Erro ao decodificar Base64 do PDF.", e);
            }
        } else if (profileDto.getDocumentoPdfBase64() == null && atleta.getDocumentoPdfBytes() != null) {
            // Se o frontend enviar null, e já existir um PDF, significa que foi removido
            atleta.setDocumentoPdfBytes(null);
            atleta.setDocumentoPdfContentType(null);
        }


        Atleta updatedAtleta = atletaRepository.save(atleta);
        return convertToDto(updatedAtleta);
    }

    public void deleteAtleta(Long atletaId) {
        Atleta atleta = findAtletaById(atletaId);
        atletaRepository.delete(atleta);
    }

    // --- Gerenciamento de Documentos PDF (principal) ---

    // Este método agora recebe um MultipartFile e salva seus bytes no DB
    public String uploadDocumentoPdf(Long atletaId, MultipartFile file) {
        Atleta atleta = findAtletaById(atletaId);

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
        Atleta atleta = findAtletaById(atletaId);
        atleta.setDocumentoPdfBytes(null);
        atleta.setDocumentoPdfContentType(null);
        atletaRepository.save(atleta);
    }

    // Se ainda precisar de upload/delete para outros documentos (DocumentoAtleta),
    // mantenha os métodos `uploadDocuments` e `deleteAtletaDocument` e suas dependências com `uploadDir`.
    // Por exemplo, `saveDocument` precisaria do `uploadDir`.
    // Se o PDF principal for o único documento, remova a classe `DocumentoAtleta` e todos os métodos relacionados a ela.

    // --- Métodos Auxiliares ---

    private Atleta findAtletaById(Long atletaId) {
        return atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));
    }

    // Converte Entidade Atleta para DTO AtletaProfileDto
    private AtletaProfileDto convertToDto(Atleta atleta) {
        AtletaProfileDto dto = new AtletaProfileDto();
        dto.setId(atleta.getId());
        dto.setMatricula(atleta.getMatricula());
        dto.setNome(atleta.getNome());
        dto.setEmail(atleta.getEmail());
        dto.setSubDivisao(atleta.getSubDivisao());
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
    private AtletaProfileDto.DocumentoDto convertToDto(DocumentoAtleta documento) {
        AtletaProfileDto.DocumentoDto dto = new AtletaProfileDto.DocumentoDto();
        dto.setId(documento.getId());
        dto.setNome(documento.getNome());
        dto.setUrl(documento.getUrl()); // Esta URL ainda existiria se outros documentos forem salvos no disco
        dto.setTipo(documento.getTipo());
        return dto;
    }
}
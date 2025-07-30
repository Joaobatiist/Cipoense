package com.br.Service;

import com.br.Response.AtletaProfileDto;
import com.br.Entity.Atleta;
import com.br.Entity.DocumentoAtleta;
import com.br.Entity.Responsavel;
import com.br.Repository.AtletaRepository;
import com.br.Repository.DocumentoAtletaRepository;
import com.br.Repository.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AtletaProfileService {

    private final AtletaRepository atletaRepository;
    private final DocumentoAtletaRepository documentoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final String uploadDir;

   
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AtletaProfileService(
            AtletaRepository atletaRepository,
            DocumentoAtletaRepository documentoRepository,
            ResponsavelRepository responsavelRepository,
            @Value("${file.upload-dir}") String uploadDir) {
        this.atletaRepository = atletaRepository;
        this.documentoRepository = documentoRepository;
        this.responsavelRepository = responsavelRepository;
        this.uploadDir = uploadDir;
    }

    public AtletaProfileDto getProfile(Long atletaId) {
        Atleta atleta = findAtletaById(atletaId);
        return convertToDto(atleta);
    }

    public AtletaProfileDto updateProfile(Long atletaId, AtletaProfileDto profileDto) {
        Atleta atleta = findAtletaById(atletaId);

        atleta.setNome(profileDto.getNome());
        atleta.setEmail(profileDto.getEmail());
        atleta.setSubDivisao(profileDto.getSubDivisao());

        // Adaptação para o formato de data (DD/MM/YYYY do frontend para LocalDate do DB)
        if (profileDto.getDataNascimento() != null && !profileDto.getDataNascimento().trim().isEmpty()) {
            try {
                // Tenta fazer o parse usando o DATE_FORMATTER
                LocalDate parsedDate = LocalDate.parse(profileDto.getDataNascimento(), DATE_FORMATTER);
                atleta.setDataNascimento(parsedDate);
            } catch (DateTimeParseException e) {
                // Se falhar com DD/MM/YYYY, tenta YYYY-MM-DD caso venha de outro lugar ou seja um erro de formatação
                try {
                    LocalDate parsedDate = LocalDate.parse(profileDto.getDataNascimento()); // Tenta o formato ISO padrão (YYYY-MM-DD)
                    atleta.setDataNascimento(parsedDate);
                } catch (DateTimeParseException ex) {
                    throw new RuntimeException("Formato de data de nascimento inválido. Use DD/MM/YYYY ou YYYY-MM-DD.", ex);
                }
            }
        } else {
            atleta.setDataNascimento(null);
        }

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

        Atleta updatedAtleta = atletaRepository.save(atleta);
        return convertToDto(updatedAtleta);
    }


    public String uploadPhoto(Long atletaId, MultipartFile file) {
        Atleta atleta = findAtletaById(atletaId);

        try {
            byte[] bytes = file.getBytes();
            atleta.setFoto(bytes);
            atleta.setFotoContentType(file.getContentType());
            atletaRepository.save(atleta);
            return "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar a imagem do perfil.", e);
        }
    }

    public List<AtletaProfileDto.DocumentoDto> uploadDocuments(Long atletaId, MultipartFile[] files) {
        findAtletaById(atletaId); // Garante que o atleta exista
        return Arrays.stream(files)
                .map(file -> saveDocument(atletaId, file))
                .collect(Collectors.toList());
    }

    private AtletaProfileDto.DocumentoDto saveDocument(Long atletaId, MultipartFile file) {
        Atleta atleta = findAtletaById(atletaId);

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String fileName = "doc_" + atletaId + "_" + UUID.randomUUID() + extension;
            Path filePath = Paths.get(uploadDir, fileName);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            DocumentoAtleta documento = new DocumentoAtleta();
            documento.setNome(originalFilename);
            // Salva a URL relativa no banco
            documento.setUrl("/uploads/" + fileName);
            documento.setTipo(extension.replace(".", "").toUpperCase());
            documento.setAtleta(atleta);

            DocumentoAtleta savedDocument = documentoRepository.save(documento);
            return convertToDto(savedDocument);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o documento", e);
        }
    }

    public void deleteDocument(Long atletaId, Long documentId) {
        DocumentoAtleta documento = documentoRepository.findByIdAndAtletaId(documentId, atletaId)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado para o atleta especificado."));

        try {
            Path filePath = Paths.get(uploadDir, documento.getUrl().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
            documentoRepository.delete(documento);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao deletar o documento", e);
        }
    }

    private Atleta findAtletaById(Long atletaId) {
        return atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));
    }

    private AtletaProfileDto convertToDto(Atleta atleta) {
        AtletaProfileDto dto = new AtletaProfileDto();
        dto.setId(atleta.getId());
        dto.setMatricula(atleta.getMatricula());
        dto.setNome(atleta.getNome());
        dto.setEmail(atleta.getEmail());
        dto.setSubDivisao(atleta.getSubDivisao());
        dto.setPosicao(atleta.getPosicao());
        // Formata a data de LocalDate (do DB) para String (DD/MM/YYYY para o frontend)
        dto.setDataNascimento(atleta.getDataNascimento() != null ? atleta.getDataNascimento().format(DATE_FORMATTER) : null);

        // Este bloco já estava correto e é essencial: converte byte[] para string Base64 para o frontend
        if (atleta.getFoto() != null && atleta.getFoto().length > 0 && atleta.getFotoContentType() != null) {
            dto.setFoto("data:" + atleta.getFotoContentType() + ";base64," + Base64.getEncoder().encodeToString(atleta.getFoto()));
        } else {
            dto.setFoto(null);
        }

        if (atleta.getResponsavel() != null) {
            dto.setContatoResponsavel(atleta.getResponsavel().getTelefone());
        } else {
            dto.setContatoResponsavel(null);
        }

        if (atleta.getDocumentos() != null && !atleta.getDocumentos().isEmpty()) {
            dto.setDocumentos(atleta.getDocumentos().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setDocumentos(List.of());
        }

        return dto;
    }

    // Método para converter DocumentoAtleta para DocumentoDto - já estava correto
    private AtletaProfileDto.DocumentoDto convertToDto(DocumentoAtleta documento) {
        AtletaProfileDto.DocumentoDto dto = new AtletaProfileDto.DocumentoDto();
        dto.setId(documento.getId());
        dto.setNome(documento.getNome());
        dto.setUrl(documento.getUrl()); // Esta URL é relativa, o frontend precisaria de API_URL para acessá-la
        dto.setTipo(documento.getTipo());
        return dto;
    }
}
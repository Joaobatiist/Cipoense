package com.br.Service;

import com.br.Response.atletaProfileDto;
import com.br.Entity.atleta;
import com.br.Entity.documentoAtleta;
import com.br.Entity.responsavel;
import com.br.Repository.atletaRepository;
import com.br.Repository.documentoAtletaRepository;
import com.br.Repository.responsavelRepository;
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
public class atletaProfileService {

    private final atletaRepository atletaRepository;
    private final documentoAtletaRepository documentoRepository;
    private final responsavelRepository responsavelRepository;
    private final String uploadDir;

   
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public atletaProfileService(
            atletaRepository atletaRepository,
            documentoAtletaRepository documentoRepository,
            responsavelRepository responsavelRepository,
            @Value("${file.upload-dir}") String uploadDir) {
        this.atletaRepository = atletaRepository;
        this.documentoRepository = documentoRepository;
        this.responsavelRepository = responsavelRepository;
        this.uploadDir = uploadDir;
    }

    public atletaProfileDto getProfile(Long atletaId) {
        atleta atleta = findAtletaById(atletaId);
        return convertToDto(atleta);
    }

    public atletaProfileDto updateProfile(Long atletaId, atletaProfileDto profileDto) {
        atleta atleta = findAtletaById(atletaId);

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

        atleta updatedAtleta = atletaRepository.save(atleta);
        return convertToDto(updatedAtleta);
    }


    public String uploadPhoto(Long atletaId, MultipartFile file) {
        atleta atleta = findAtletaById(atletaId);

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

    public List<atletaProfileDto.DocumentoDto> uploadDocuments(Long atletaId, MultipartFile[] files) {
        findAtletaById(atletaId); // Garante que o atleta exista
        return Arrays.stream(files)
                .map(file -> saveDocument(atletaId, file))
                .collect(Collectors.toList());
    }

    private atletaProfileDto.DocumentoDto saveDocument(Long atletaId, MultipartFile file) {
        atleta atleta = findAtletaById(atletaId);

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

            documentoAtleta documento = new documentoAtleta();
            documento.setNome(originalFilename);
            // Salva a URL relativa no banco
            documento.setUrl("/uploads/" + fileName);
            documento.setTipo(extension.replace(".", "").toUpperCase());
            documento.setAtleta(atleta);

            documentoAtleta savedDocument = documentoRepository.save(documento);
            return convertToDto(savedDocument);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o documento", e);
        }
    }

    public void deleteDocument(Long atletaId, Long documentId) {
        documentoAtleta documento = documentoRepository.findByIdAndAtletaId(documentId, atletaId)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado para o atleta especificado."));

        try {
            Path filePath = Paths.get(uploadDir, documento.getUrl().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
            documentoRepository.delete(documento);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao deletar o documento", e);
        }
    }

    private atleta findAtletaById(Long atletaId) {
        return atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));
    }

    private atletaProfileDto convertToDto(atleta atleta) {
        atletaProfileDto dto = new atletaProfileDto();
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
    private atletaProfileDto.DocumentoDto convertToDto(documentoAtleta documento) {
        atletaProfileDto.DocumentoDto dto = new atletaProfileDto.DocumentoDto();
        dto.setId(documento.getId());
        dto.setNome(documento.getNome());
        dto.setUrl(documento.getUrl()); // Esta URL é relativa, o frontend precisaria de API_URL para acessá-la
        dto.setTipo(documento.getTipo());
        return dto;
    }
}
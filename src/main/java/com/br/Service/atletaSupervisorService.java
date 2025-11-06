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
        atleta.setIsAptoParaJogar(profileDto.getIsAptoParaJogar());
        atleta.setPosicao(profileDto.getPosicao());
        atleta.setAlergias(profileDto.getAlergias());
        atleta.setTipoSanguineo(profileDto.getTipoSanguineo());
        atleta.setIsencao(profileDto.isIsencao());
        atleta.setHorarioDeAula(profileDto.getHorarioDeAula());
        atleta.setAnoEscolar(profileDto.getAnoEscolar());
        // Ajuste para o nome do campo 'endereco' (DTO usa 'Endereco' maiúsculo)
        atleta.setEndereco(profileDto.getEndereco());
        atleta.setMassa(profileDto.getMassa());
        atleta.setAltura(profileDto.getAltura());
        atleta.setRg(profileDto.getRg());
        atleta.setProblemaDeSaude(profileDto.getProblemaDeSaude());
        atleta.setContatoEscola(profileDto.getContatoEscola());
        atleta.setEscola(profileDto.getEscola()); // Adicionado o campo 'escola'

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

        // Contato Responsável Principal
        this.updateResponsavel(atleta.getResponsavel(), profileDto.getContatoResponsavel(), atleta::setResponsavel);

        // Contato Responsável Secundário
        this.updateResponsavel(
                atleta.getContatoResponsavelSecundario(),      // ARG 1
                profileDto.getContatoResponsavelSecundario(), // ARG 2
                atleta::setContatoResponsavelSecundario
        );
        // CORREÇÃO: Se o PDF for enviado junto com a atualização do perfil (opcional, mas possível)
        if (profileDto.getDocumentoPdfBase64() != null && !profileDto.getDocumentoPdfBase64().isEmpty()) {
            try {
                String base64Data = profileDto.getDocumentoPdfBase64();
                // Remove o prefixo do Data URI se presente
                if (base64Data.contains(",")) {
                    base64Data = base64Data.split(",")[1];
                }
                atleta.setDocumentoPdfBytes(base64Data);
                atleta.setDocumentoPdfContentType(profileDto.getDocumentoPdfContentType());
            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar Base64 do PDF: " + e.getMessage(), e);
            }
        }

        atleta updatedAtleta = atletaRepository.save(atleta);
        return convertToDto(updatedAtleta);
    }

    // Método auxiliar para atualizar os responsáveis
    private void updateResponsavel(responsavel currentResponsavel, String newContact, java.util.function.Consumer<responsavel> setter) {
        if (newContact != null && !newContact.trim().isEmpty()) {
            responsavel responsavelToUpdate = currentResponsavel;
            if (responsavelToUpdate == null) {
                responsavelToUpdate = new responsavel();
                setter.accept(responsavelToUpdate); // Vincula o novo responsável ao atleta
            }
            responsavelToUpdate.setTelefone(newContact);
            responsavelRepository.save(responsavelToUpdate);
        } else {
            if (currentResponsavel != null) {
                // Remove apenas o telefone, mantendo a entidade para evitar remoção em cascata indesejada se houver outros dados.
                currentResponsavel.setTelefone(null);
                responsavelRepository.save(currentResponsavel);
                // Se a intenção for realmente remover a entidade se o contato estiver vazio, use:
                // responsavelRepository.delete(currentResponsavel);
                // setter.accept(null);
            }
        }
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

    // CORREÇÃO ESSENCIAL: Mapeamento completo de Entidade Atleta para DTO AtletaProfileDto
    private atletaProfileDto convertToDto(atleta atleta) {
        atletaProfileDto dto = new atletaProfileDto();

        // --- 1. CAMPOS ESSENCIAIS E ORGANIZACIONAIS ---
        dto.setId(atleta.getId());
        dto.setMatricula(atleta.getMatricula());
        dto.setNome(atleta.getNome());
        dto.setEmail(atleta.getEmail());
        dto.setRg(atleta.getRg());
        dto.setSubDivisao(atleta.getSubDivisao());
        dto.setPosicao(atleta.getPosicao());
        dto.setIsAptoParaJogar(atleta.getIsAptoParaJogar());
        dto.setIsencao(atleta.isIsencao());

        // --- 2. CARACTERÍSTICAS E SAÚDE ---
        dto.setMassa(atleta.getMassa());
        dto.setAltura(atleta.getAltura());
        dto.setTipoSanguineo(atleta.getTipoSanguineo());
        dto.setAlergias(atleta.getAlergias());
        dto.setProblemaDeSaude(atleta.getProblemaDeSaude());

        // --- 3. ESCOLA E ENDEREÇO ---
        // Se o DTO tem 'Endereco' (com 'E' maiúsculo), o setter correto será usado
        dto.setEndereco(atleta.getEndereco());
        dto.setEscola(atleta.getEscola());
        dto.setContatoEscola(atleta.getContatoEscola());
        dto.setAnoEscolar(atleta.getAnoEscolar());
        dto.setHorarioDeAula(atleta.getHorarioDeAula());

        // --- 4. DATA DE NASCIMENTO ---
        dto.setDataNascimento(atleta.getDataNascimento() != null ? atleta.getDataNascimento().format(DATE_FORMATTER) : null);

        // --- 5. RESPONSÁVEIS (RELAÇÃO @OneToOne) ---
        // Contato Responsável Principal (usando getTelefone() da Entidade responsavel)
        if (atleta.getResponsavel() != null) {
            dto.setContatoResponsavel(atleta.getResponsavel().getTelefone());
        } else {
            dto.setContatoResponsavel(null);
        }

        // Contato Responsável Secundário
        if (atleta.getContatoResponsavelSecundario() != null) {
            dto.setContatoResponsavelSecundario(atleta.getContatoResponsavelSecundario().getTelefone());
        } else {
            dto.setContatoResponsavelSecundario(null);
        }

        // --- 6. ARQUIVOS (Base64) ---
        // Foto
        if (atleta.getFoto() != null && !atleta.getFoto().isEmpty() && atleta.getFotoContentType() != null) {
            dto.setFoto("data:" + atleta.getFotoContentType() + ";base64," + atleta.getFoto());
        } else {
            dto.setFoto(null);
        }

        // PDF Principal
        if (atleta.getDocumentoPdfBytes() != null && !atleta.getDocumentoPdfBytes().isEmpty() && atleta.getDocumentoPdfContentType() != null) {
            dto.setDocumentoPdfBase64(atleta.getDocumentoPdfBytes());
            dto.setDocumentoPdfContentType(atleta.getDocumentoPdfContentType());
        } else {
            dto.setDocumentoPdfBase64(null);
            dto.setDocumentoPdfContentType(null);
        }

        // --- 7. OUTROS DOCUMENTOS (Lista) ---
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
        dto.setUrl(documento.getUrl());
        dto.setTipo(documento.getTipo());
        return dto;
    }
}
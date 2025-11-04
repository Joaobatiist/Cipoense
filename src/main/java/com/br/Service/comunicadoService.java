package com.br.Service;

import com.br.Entity.*;
import com.br.Enums.role; // Certifique-se que o import está correto
import com.br.Repository.*;
import com.br.Request.comunicadoRequest;
import com.br.Response.comunicadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class comunicadoService {

    private final comunicadoRepository comunicadoRepository;
    private final atletaRepository atletaRepository;
    private final comunicadoStatusRepository comunicadoStatusPorUsuarioRepository;
    private final funcionarioRepository funcionarioRepository; // Único repositório para funcionários

    @Autowired
    public comunicadoService(comunicadoRepository comunicadoRepository, atletaRepository atletaRepository, comunicadoStatusRepository comunicadoStatusPorUsuarioRepository, funcionarioRepository funcionarioRepository) {
        this.comunicadoRepository = comunicadoRepository;
        this.atletaRepository = atletaRepository;
        this.comunicadoStatusPorUsuarioRepository = comunicadoStatusPorUsuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    // ⭐ ALTERAÇÃO 1: Método reescrito para lidar com os papéis unificados.
    protected Object getLoggedInUserEntity(String username, Set<GrantedAuthority> authorities) {
        // Extrai o papel (role) do usuário.
        String userRole = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                .findFirst()
                .orElse("");

        // Verifica se o papel pertence à categoria Funcionario (COORDENADOR, SUPERVISOR, TECNICO)
        if (userRole.equals(role.COORDENADOR.name()) ||
                userRole.equals(role.SUPERVISOR.name()) ||
                userRole.equals(role.TECNICO.name())) {

            // Busca na tabela unificada de Funcionario
            return funcionarioRepository.findByEmail(username).orElse(null);

        } else if (userRole.equals(role.ATLETA.name())) {

            // Busca na tabela de Atleta
            return atletaRepository.findByEmail(username).orElse(null);

        } else {
            // Se o papel não for nenhum dos esperados, retorna null.
            return null;
        }
    }

    @Transactional
    public comunicadoResponse criarComunicado(comunicadoRequest dto) {
        comunicado comunicado = new comunicado();
        comunicado.setAssunto(dto.getAssunto());
        comunicado.setMensagem(dto.getMensagem());
        comunicado.setData(dto.getData() != null ? dto.getData() : LocalDate.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Object loggedInUser = getLoggedInUserEntity(username, new HashSet<>(authentication.getAuthorities()));

        // Define o remetente
        if (loggedInUser instanceof funcionario) {
            comunicado.setRemetenteFuncionario((funcionario) loggedInUser);
        } else {
            // Se o remetente não for um funcionário, lança erro.
            String userType = (loggedInUser != null) ? loggedInUser.getClass().getSimpleName() : "null ou tipo desconhecido";
            String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "));
            throw new RuntimeException("Usuário do tipo '" + userType + "' com papéis [" + roles + "] não tem autorização para criar comunicados.");
        }

        List<Object> allDestinatarios = processarDestinatarios(comunicado, dto);
        comunicado savedComunicado = comunicadoRepository.save(comunicado);

        for (Object dest : allDestinatarios) {
            criarOuAtualizarStatus(savedComunicado, dest, false);
        }

        return convertToDto(savedComunicado);
    }

    private comunicadoResponse convertToDto(comunicado comunicado) {
        comunicadoResponse dto = new comunicadoResponse();
        dto.setId(comunicado.getId());
        dto.setAssunto(comunicado.getAssunto());
        dto.setMensagem(comunicado.getMensagem());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (comunicado.getData() != null) {
            dto.setDataEnvio(comunicado.getData().format(formatter));
        }

        List<comunicadoResponse.DestinatarioDTO> destinatarios = new ArrayList<>();
        if (comunicado.getDestinatariosAtletas() != null) {
            comunicado.getDestinatariosAtletas().forEach(atleta ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(atleta.getId(), atleta.getNome(), role.ATLETA.name()))
            );
        }

        if (comunicado.getDestinatariosFuncionarios() != null) {
            comunicado.getDestinatariosFuncionarios().forEach(funcionario -> {
                // Usa a Role específica do funcionário (COORDENADOR, SUPERVISOR, TECNICO)
                String cargoDoFuncionario = funcionario.getRole().name();
                destinatarios.add(new comunicadoResponse.DestinatarioDTO(
                        funcionario.getId(),
                        funcionario.getNome(),
                        cargoDoFuncionario
                ));
            });
        }
        dto.setDestinatarios(destinatarios);

        // Remetente funcionario (UNIFICADO)
        if (comunicado.getRemetenteFuncionario() != null) {
            funcionario remetente = comunicado.getRemetenteFuncionario();
            // Usa a Role específica do remetente
            String cargoDoFuncionario = remetente.getRole().name();

            dto.setRemetente(new comunicadoResponse.DestinatarioDTO(
                    remetente.getId(),
                    remetente.getNome(),
                    cargoDoFuncionario
            ));
        }
        return dto;
    }


    @Transactional
    public comunicadoResponse atualizarComunicado(UUID id, comunicadoRequest dto) {
        comunicado comunicado = comunicadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado com ID: " + id));
        // Lógica de permissão...
        comunicado.setAssunto(dto.getAssunto());
        comunicado.setMensagem(dto.getMensagem());
        comunicado.setData(dto.getData() != null ? dto.getData() : comunicado.getData());

        // Limpar destinatários antes de adicionar os novos
        comunicado.getDestinatariosAtletas().clear();
        comunicado.getDestinatariosFuncionarios().clear();

        // Recria os status (idealmente, deveria-se buscar e atualizar os existentes, mas seguindo a lógica original de processamento)
        List<Object> allNewDestinatarios = processarDestinatarios(comunicado, dto);

        // Apaga os status antigos e cria novos para os destinatários atualizados
        comunicadoStatusPorUsuarioRepository.deleteAll(comunicadoStatusPorUsuarioRepository.findByComunicado(comunicado));

        comunicado updatedComunicado = comunicadoRepository.save(comunicado);

        for (Object dest : allNewDestinatarios) {
            criarOuAtualizarStatus(updatedComunicado, dest, false);
        }

        return convertToDto(updatedComunicado);
    }

    @Transactional(readOnly = true)
    public List<comunicadoResponse> buscarTodosComunicados() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Set<comunicado> comunicadosDoUsuario = new LinkedHashSet<>();

        Object loggedInUser = getLoggedInUserEntity(username, authorities);
        if (loggedInUser == null) {
            return Collections.emptyList();
        }

        // Se for ADMIN, busca todos
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findAllWithAllDetails());
        } else {
            UUID loggedInUserId = getUserId(loggedInUser);
            if(loggedInUserId == null) return Collections.emptyList();

            // Busca comunicados enviados pelo usuário (se for um funcionário)
            if (loggedInUser instanceof funcionario) {
                comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteFuncionarioWithDetails((funcionario) loggedInUser));
            }

            // Busca comunicados onde o usuário é destinatário
            if (loggedInUser instanceof atleta) {
                comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioAtletaIdAndNotOcultado(loggedInUserId));
            } else if (loggedInUser instanceof funcionario) {
                // ⭐ ALTERAÇÃO 2: Busca unificada para todos os funcionários (Coordenador, Supervisor, Tecnico)
                comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioFuncionarioIdAndNotOcultado(loggedInUserId));
            }
        }

        return comunicadosDoUsuario.stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(comunicadoResponse::getDataEnvio, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<comunicadoResponse> buscarComunicadoPorId(UUID id) {
        // Lógica de permissão...
        return comunicadoRepository.findByIdWithAllDetails(id).map(this::convertToDto);
    }

    @Transactional
    public void ocultarComunicadoParaUsuario(UUID comunicadoId) {
        // Lógica...
    }

    @Transactional
    public void deletarComunicadoPermanentemente(UUID id) {
        // Lógica de permissão...
        comunicado comunicado = comunicadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Comunicado não encontrado"));
        comunicadoStatusPorUsuarioRepository.deleteAll(comunicadoStatusPorUsuarioRepository.findByComunicado(comunicado));
        comunicadoRepository.delete(comunicado);
    }

    private List<Object> processarDestinatarios(comunicado comunicado, comunicadoRequest dto) {
        List<Object> allDestinatarios = new ArrayList<>();

        if (dto.getAtletasIds() != null && !dto.getAtletasIds().isEmpty()) {
            List<atleta> atletas = atletaRepository.findAllById(dto.getAtletasIds());
            atletas.forEach(comunicado::addDestinatarioAtleta);
            allDestinatarios.addAll(atletas);
        }
        if (dto.getFuncionarioIds() != null && !dto.getFuncionarioIds().isEmpty()) {
            List<funcionario> funcionarios = funcionarioRepository.findAllById(dto.getFuncionarioIds());
            funcionarios.forEach(comunicado::addDestinatarioFuncionario);
            allDestinatarios.addAll(funcionarios);
        }
        return allDestinatarios;
    }

    // ⭐ ALTERAÇÃO 3: Método getUserId unificado.
    private UUID getUserId(Object userEntity) {
        if (userEntity instanceof atleta) return ((atleta) userEntity).getId();
        // Checagem única para todos os funcionários
        if (userEntity instanceof funcionario) return ((funcionario) userEntity).getId();
        return null;
    }

    // ⭐ ALTERAÇÃO 4: Método criarOuAtualizarStatus unificado.
    private void criarOuAtualizarStatus(comunicado c, Object dest, boolean ocultado) {
        comunicadoStatus status = new comunicadoStatus();
        status.setComunicado(c);

        if (dest instanceof atleta) {
            status.setAtleta((atleta) dest);
        } else if (dest instanceof funcionario) {
            // Usa o relacionamento único para Funcionario
            status.setFuncionario((funcionario) dest);
        }
        // Removida a lógica de else if (dest instanceof coordenador), supervisor, tecnico.

        status.setOcultado(ocultado);
        comunicadoStatusPorUsuarioRepository.save(status);
    }
}
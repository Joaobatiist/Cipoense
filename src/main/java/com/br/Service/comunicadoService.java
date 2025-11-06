package com.br.Service;

import com.br.Entity.*;
import com.br.Enums.role;
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
    private final funcionarioRepository funcionarioRepository;

    @Autowired
    public comunicadoService(comunicadoRepository comunicadoRepository, atletaRepository atletaRepository, comunicadoStatusRepository comunicadoStatusPorUsuarioRepository, funcionarioRepository funcionarioRepository) {
        this.comunicadoRepository = comunicadoRepository;
        this.atletaRepository = atletaRepository;
        this.comunicadoStatusPorUsuarioRepository = comunicadoStatusPorUsuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    protected Object getLoggedInUserEntity(String username, Set<GrantedAuthority> authorities) {
        String userRole = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                .findFirst()
                .orElse("");

        if (userRole.equals(role.COORDENADOR.name()) ||
                userRole.equals(role.SUPERVISOR.name()) ||
                userRole.equals(role.TECNICO.name())) {
            return funcionarioRepository.findByEmail(username).orElse(null);
        } else if (userRole.equals(role.ATLETA.name())) {
            return atletaRepository.findByEmail(username).orElse(null);
        } else {
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

        if (loggedInUser instanceof funcionario) {
            comunicado.setRemetenteFuncionario((funcionario) loggedInUser);
        } else {
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
                String cargoDoFuncionario = funcionario.getRole().name();
                destinatarios.add(new comunicadoResponse.DestinatarioDTO(
                        funcionario.getId(),
                        funcionario.getNome(),
                        cargoDoFuncionario
                ));
            });
        }
        dto.setDestinatarios(destinatarios);

        if (comunicado.getRemetenteFuncionario() != null) {
            funcionario remetente = comunicado.getRemetenteFuncionario();
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

        comunicado.setAssunto(dto.getAssunto());
        comunicado.setMensagem(dto.getMensagem());
        comunicado.setData(dto.getData() != null ? dto.getData() : comunicado.getData());

        comunicado.getDestinatariosAtletas().clear();
        comunicado.getDestinatariosFuncionarios().clear();

        List<Object> allNewDestinatarios = processarDestinatarios(comunicado, dto);

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

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findAllWithAllDetails());
        } else {
            UUID loggedInUserId = getUserId(loggedInUser);
            if(loggedInUserId == null) return Collections.emptyList();

            if (loggedInUser instanceof funcionario) {
                comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteFuncionarioWithDetails((funcionario) loggedInUser));
            }

            if (loggedInUser instanceof atleta) {
                comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioAtletaIdAndNotOcultado(loggedInUserId));
            } else if (loggedInUser instanceof funcionario) {
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
        return comunicadoRepository.findByIdWithAllDetails(id).map(this::convertToDto);
    }

    @Transactional
    public void ocultarComunicadoParaUsuario(UUID comunicadoId) {
        // Implementar lógica
    }

    @Transactional
    public void deletarComunicadoPermanentemente(UUID id) {
        comunicado comunicado = comunicadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Comunicado não encontrado"));
        comunicadoStatusPorUsuarioRepository.deleteAll(comunicadoStatusPorUsuarioRepository.findByComunicado(comunicado));
        comunicadoRepository.delete(comunicado);
    }

    // 🎯 CORREÇÃO PRINCIPAL: Método processarDestinatarios atualizado
    private List<Object> processarDestinatarios(comunicado comunicado, comunicadoRequest dto) {
        List<Object> allDestinatarios = new ArrayList<>();

        // Processar atletas
        if (dto.getAtletasIds() != null && !dto.getAtletasIds().isEmpty()) {
            System.out.println("🏃‍♂️ Processando " + dto.getAtletasIds().size() + " atletas: " + dto.getAtletasIds());
            List<atleta> atletas = atletaRepository.findAllById(dto.getAtletasIds());
            System.out.println("🏃‍♂️ Encontrados " + atletas.size() + " atletas no banco");
            atletas.forEach(comunicado::addDestinatarioAtleta);
            allDestinatarios.addAll(atletas);
        }

        // 🔧 NOVA LÓGICA: Processar todos os tipos de funcionários
        List<UUID> todosFuncionarioIds = new ArrayList<>();

        // Coletar IDs de coordenadores
        if (dto.getCoordenadorIds() != null && !dto.getCoordenadorIds().isEmpty()) {
            System.out.println("👔 Processando " + dto.getCoordenadorIds().size() + " coordenadores: " + dto.getCoordenadorIds());
            todosFuncionarioIds.addAll(dto.getCoordenadorIds());
        }

        // Coletar IDs de supervisores
        if (dto.getSupervisorIds() != null && !dto.getSupervisorIds().isEmpty()) {
            System.out.println("👔 Processando " + dto.getSupervisorIds().size() + " supervisores: " + dto.getSupervisorIds());
            todosFuncionarioIds.addAll(dto.getSupervisorIds());
        }

        // Coletar IDs de técnicos
        if (dto.getTecnicoIds() != null && !dto.getTecnicoIds().isEmpty()) {
            System.out.println("👔 Processando " + dto.getTecnicoIds().size() + " técnicos: " + dto.getTecnicoIds());
            todosFuncionarioIds.addAll(dto.getTecnicoIds());
        }

        // Buscar todos os funcionários de uma vez
        if (!todosFuncionarioIds.isEmpty()) {
            System.out.println("👔 Total de funcionários para buscar: " + todosFuncionarioIds.size());
            System.out.println("👔 IDs: " + todosFuncionarioIds);

            List<funcionario> funcionarios = funcionarioRepository.findAllById(todosFuncionarioIds);
            System.out.println("👔 Encontrados " + funcionarios.size() + " funcionários no banco");

            // Debug: Mostrar cada funcionário encontrado
            funcionarios.forEach(func -> {
                System.out.println("👔 Funcionário: " + func.getNome() + " (" + func.getRole() + ") - ID: " + func.getId());
            });

            funcionarios.forEach(comunicado::addDestinatarioFuncionario);
            allDestinatarios.addAll(funcionarios);
        }

        System.out.println("📊 Total de destinatários processados: " + allDestinatarios.size());
        return allDestinatarios;
    }

    private UUID getUserId(Object userEntity) {
        if (userEntity instanceof atleta) return ((atleta) userEntity).getId();
        if (userEntity instanceof funcionario) return ((funcionario) userEntity).getId();
        return null;
    }

    private void criarOuAtualizarStatus(comunicado c, Object dest, boolean ocultado) {
        comunicadoStatus status = new comunicadoStatus();
        status.setComunicado(c);

        if (dest instanceof atleta) {
            System.out.println("📝 Criando status para atleta: " + ((atleta) dest).getNome());
            status.setAtleta((atleta) dest);
        } else if (dest instanceof funcionario) {
            System.out.println("📝 Criando status para funcionário: " + ((funcionario) dest).getNome() + " (" + ((funcionario) dest).getRole() + ")");
            status.setFuncionario((funcionario) dest);
        }

        status.setOcultado(ocultado);
        comunicadoStatusPorUsuarioRepository.save(status);
        System.out.println("✅ Status salvo com sucesso");
    }
}
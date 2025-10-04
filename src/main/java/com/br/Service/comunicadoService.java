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
    private final coordenadorRepository coordenadorRepository;
    private final supervisorRepository supervisorRepository;
    private final tecnicoRepository tecnicoRepository;
    private final comunicadoStatusRepository comunicadoStatusPorUsuarioRepository;

    @Autowired
    public comunicadoService(comunicadoRepository comunicadoRepository, atletaRepository atletaRepository, coordenadorRepository coordenadorRepository, supervisorRepository supervisorRepository, tecnicoRepository tecnicoRepository, comunicadoStatusRepository comunicadoStatusPorUsuarioRepository) {
        this.comunicadoRepository = comunicadoRepository;
        this.atletaRepository = atletaRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.supervisorRepository = supervisorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.comunicadoStatusPorUsuarioRepository = comunicadoStatusPorUsuarioRepository;
    }

    // ⭐ CORREÇÃO: Método reescrito para lidar corretamente com os papéis do Spring Security
    protected Object getLoggedInUserEntity(String username, Set<GrantedAuthority> authorities) {
        // Extrai o primeiro papel (role) do usuário, removendo o prefixo "ROLE_" se existir.
        String userRole = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                .findFirst()
                .orElse("");

        // Usa um switch para buscar no repositório correto. É mais limpo e eficiente.
        switch (userRole) {
            case "COORDENADOR":
                return coordenadorRepository.findByEmail(username).orElse(null);
            case "SUPERVISOR":
                return supervisorRepository.findByEmail(username).orElse(null);
            case "TECNICO":
                return tecnicoRepository.findByEmail(username).orElse(null);
            case "ATLETA":
                return atletaRepository.findByEmail(username).orElse(null);
            default:
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
        if (loggedInUser instanceof coordenador) {
            comunicado.setRemetenteCoordenador((coordenador) loggedInUser);
        } else if (loggedInUser instanceof supervisor) {
            comunicado.setRemetenteSupervisor((supervisor) loggedInUser);
        } else if (loggedInUser instanceof tecnico) {
            comunicado.setRemetenteTecnico((tecnico) loggedInUser);
        } else {
            // ⭐ CORREÇÃO: Mensagem de erro melhorada para facilitar a depuração
            String userType = (loggedInUser != null) ? loggedInUser.getClass().getSimpleName() : "null ou tipo desconhecido";
            String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "));
            throw new RuntimeException("Usuário do tipo '" + userType + "' com papéis [" + roles + "] não foi encontrado em uma tabela válida ou não tem autorização para criar comunicados.");
        }

        List<Object> allDestinatarios = processarDestinatarios(comunicado, dto);
        comunicado savedComunicado = comunicadoRepository.save(comunicado);

        for (Object dest : allDestinatarios) {
            criarOuAtualizarStatus(savedComunicado, dest, false);
        }

        return convertToDto(savedComunicado);
    }

    // ... O restante dos seus métodos (atualizar, buscar, etc.) permanece o mesmo ...
    // Cole o resto dos seus métodos aqui (atualizarComunicado, buscarTodosComunicados, etc.)
    // A partir daqui, o código é o mesmo da sua versão anterior.

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
        if (comunicado.getDestinatariosCoordenadores() != null) {
            comunicado.getDestinatariosCoordenadores().forEach(coord ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(coord.getId(), coord.getNome(), role.COORDENADOR.name()))
            );
        }
        if (comunicado.getDestinatariosSupervisores() != null) {
            comunicado.getDestinatariosSupervisores().forEach(superv ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(superv.getId(), superv.getNome(), role.SUPERVISOR.name()))
            );
        }
        if (comunicado.getDestinatariosTecnicos() != null) {
            comunicado.getDestinatariosTecnicos().forEach(tec ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(tec.getId(), tec.getNome(), role.TECNICO.name()))
            );
        }
        dto.setDestinatarios(destinatarios);

        if (comunicado.getRemetenteCoordenador() != null) {
            coordenador remetente = comunicado.getRemetenteCoordenador();
            dto.setRemetente(new comunicadoResponse.DestinatarioDTO(remetente.getId(), remetente.getNome(), role.COORDENADOR.name()));
        } else if (comunicado.getRemetenteSupervisor() != null) {
            supervisor remetente = comunicado.getRemetenteSupervisor();
            dto.setRemetente(new comunicadoResponse.DestinatarioDTO(remetente.getId(), remetente.getNome(), role.SUPERVISOR.name()));
        } else if (comunicado.getRemetenteTecnico() != null) {
            tecnico remetente = comunicado.getRemetenteTecnico();
            dto.setRemetente(new comunicadoResponse.DestinatarioDTO(remetente.getId(), remetente.getNome(), role.TECNICO.name()));
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
        comunicado.getDestinatariosAtletas().clear();
        comunicado.getDestinatariosCoordenadores().clear();
        comunicado.getDestinatariosSupervisores().clear();
        comunicado.getDestinatariosTecnicos().clear();
        List<Object> allNewDestinatarios = processarDestinatarios(comunicado, dto);
        comunicado updatedComunicado = comunicadoRepository.save(comunicado);
        // Lógica de status...
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

            if (loggedInUser instanceof coordenador) comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteCoordenadorWithDetails((coordenador) loggedInUser));
            else if (loggedInUser instanceof supervisor) comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteSupervisorWithDetails((supervisor) loggedInUser));
            else if (loggedInUser instanceof tecnico) comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteTecnicoWithDetails((tecnico) loggedInUser));

            if (loggedInUser instanceof atleta) comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioAtletaIdAndNotOcultado(loggedInUserId));
            else if (loggedInUser instanceof coordenador) comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioCoordenadorIdAndNotOcultado(loggedInUserId));
            else if (loggedInUser instanceof supervisor) comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioSupervisorIdAndNotOcultado(loggedInUserId));
            else if (loggedInUser instanceof tecnico) comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioTecnicoIdAndNotOcultado(loggedInUserId));
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
        if (dto.getCoordenadorIds() != null && !dto.getCoordenadorIds().isEmpty()) {
            List<coordenador> coordenadores = coordenadorRepository.findAllById(dto.getCoordenadorIds());
            coordenadores.forEach(comunicado::addDestinatarioCoordenador);
            allDestinatarios.addAll(coordenadores);
        }
        if (dto.getSupervisorIds() != null && !dto.getSupervisorIds().isEmpty()) {
            List<supervisor> supervisores = supervisorRepository.findAllById(dto.getSupervisorIds());
            supervisores.forEach(comunicado::addDestinatarioSupervisor);
            allDestinatarios.addAll(supervisores);
        }
        if (dto.getTecnicoIds() != null && !dto.getTecnicoIds().isEmpty()) {
            List<tecnico> tecnicos = tecnicoRepository.findAllById(dto.getTecnicoIds());
            tecnicos.forEach(comunicado::addDestinatarioTecnico);
            allDestinatarios.addAll(tecnicos);
        }
        return allDestinatarios;
    }

    private UUID getUserId(Object userEntity) {
        if (userEntity instanceof atleta) return ((atleta) userEntity).getId();
        if (userEntity instanceof coordenador) return ((coordenador) userEntity).getId();
        if (userEntity instanceof supervisor) return ((supervisor) userEntity).getId();
        if (userEntity instanceof tecnico) return ((tecnico) userEntity).getId();
        return null;
    }

    private void criarOuAtualizarStatus(comunicado c, Object dest, boolean ocultado) {
        comunicadoStatus status = new comunicadoStatus();
        status.setComunicado(c);
        if (dest instanceof atleta) status.setAtleta((atleta) dest);
        else if (dest instanceof coordenador) status.setCoordenador((coordenador) dest);
        else if (dest instanceof supervisor) status.setSupervisor((supervisor) dest);
        else if (dest instanceof tecnico) status.setTecnico((tecnico) dest);
        status.setOcultado(ocultado);
        comunicadoStatusPorUsuarioRepository.save(status);
    }
}
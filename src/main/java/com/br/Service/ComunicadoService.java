package com.br.Service;

import com.br.Entity.*;
import com.br.Enums.Role;
import com.br.Repository.*;
import com.br.Request.ComunicadoRequest;
import com.br.Response.ComunicadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections; // Importe este
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator; // Importe este
import java.util.stream.Collectors;

@Service
public class ComunicadoService {

    @Autowired
    private ComunicadoRepository comunicadoRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired // NOVO: Injete o novo repositório
    private ComunicadoStatusRepository comunicadoStatusPorUsuarioRepository;


    @Transactional(readOnly = true)
    protected Object getLoggedInUserEntity(String username, Set<GrantedAuthority> authorities) {


        if (authorities.stream().anyMatch(a -> a.getAuthority().equals( Role.COORDENADOR.name()))) {
            return coordenadorRepository.findByEmail(username).orElse(null);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals( Role.SUPERVISOR.name()))) {
            return supervisorRepository.findByEmail(username).orElse(null);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals( Role.TECNICO.name()))) {
            return tecnicoRepository.findByEmail(username).orElse(null);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals( Role.ATLETA.name()))) {
            return atletaRepository.findByEmail(username).orElse(null);
        }
        return null;
    }


    private ComunicadoResponse convertToDto(Comunicado comunicado) {
        ComunicadoResponse dto = new ComunicadoResponse();
        dto.setId(comunicado.getId());
        dto.setAssunto(comunicado.getAssunto());
        dto.setMensagem(comunicado.getMensagem());
        dto.setDataEnvio(comunicado.getData()); // Use DataEnvio para consistência com o frontend

        List<ComunicadoResponse.DestinatarioDTO> destinatarios = new ArrayList<>();


        if (comunicado.getDestinatariosAtletas() != null) {
            comunicado.getDestinatariosAtletas().forEach(atleta ->
                    destinatarios.add(new ComunicadoResponse.DestinatarioDTO(atleta.getId(), atleta.getNome(), Role.ATLETA.name()))
            );
        }
        // Adiciona destinatários Coordenadores
        if (comunicado.getDestinatariosCoordenadores() != null) {
            comunicado.getDestinatariosCoordenadores().forEach(coord ->
                    destinatarios.add(new ComunicadoResponse.DestinatarioDTO(coord.getId(), coord.getNome(), Role.COORDENADOR.name()))
            );
        }
        // Adiciona destinatários Supervisores
        if (comunicado.getDestinatariosSupervisores() != null) {
            comunicado.getDestinatariosSupervisores().forEach(superv ->
                    destinatarios.add(new ComunicadoResponse.DestinatarioDTO(superv.getId(), superv.getNome(), Role.SUPERVISOR.name()))
            );
        }
        // Adiciona destinatários Técnicos
        if (comunicado.getDestinatariosTecnicos() != null) {
            comunicado.getDestinatariosTecnicos().forEach(tec ->
                    destinatarios.add(new ComunicadoResponse.DestinatarioDTO(tec.getId(), tec.getNome(), Role.TECNICO.name()))
            );
        }

        dto.setDestinatarios(destinatarios);

        // Lógica para mapear o remetente
        if (comunicado.getRemetenteCoordenador() != null) {
            Coordenador remetente = comunicado.getRemetenteCoordenador();
            dto.setRemetente(new ComunicadoResponse.DestinatarioDTO(remetente.getId(), remetente.getNome(), Role.COORDENADOR.name()));
        } else if (comunicado.getRemetenteSupervisor() != null) {
            Supervisor remetente = comunicado.getRemetenteSupervisor();
            dto.setRemetente(new ComunicadoResponse.DestinatarioDTO(remetente.getId(), remetente.getNome(), Role.SUPERVISOR.name()));
        } else if (comunicado.getRemetenteTecnico() != null) {
            Tecnico remetente = comunicado.getRemetenteTecnico();
            dto.setRemetente(new ComunicadoResponse.DestinatarioDTO(remetente.getId(), remetente.getNome(), Role.TECNICO.name()));
        }
        return dto;
    }

    @Transactional
    public ComunicadoResponse criarComunicado(ComunicadoRequest dto) {
        Comunicado comunicado = new Comunicado();
        comunicado.setAssunto(dto.getAssunto());
        comunicado.setMensagem(dto.getMensagem());
        comunicado.setData(dto.getData() != null ? dto.getData() : LocalDate.now()); // Data de envio

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());

        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        if (loggedInUser instanceof Coordenador) {
            comunicado.setRemetenteCoordenador((Coordenador) loggedInUser);
        } else if (loggedInUser instanceof Supervisor) {
            comunicado.setRemetenteSupervisor((Supervisor) loggedInUser);
        } else if (loggedInUser instanceof Tecnico) {
            comunicado.setRemetenteTecnico((Tecnico) loggedInUser);
        } else {
            throw new RuntimeException("Usuário autenticado (" + username + ") não tem permissão para criar comunicados.");
        }

        List<Object> allDestinatarios = new ArrayList<>(); // Lista para armazenar todos os objetos destinatários

        if (dto.getAtletasIds() != null && !dto.getAtletasIds().isEmpty()) {
            List<Atleta> atletas = atletaRepository.findAllById(dto.getAtletasIds());
            atletas.forEach(comunicado::addDestinatarioAtleta);
            allDestinatarios.addAll(atletas);
        }
        if (dto.getCoordenadorIds() != null && !dto.getCoordenadorIds().isEmpty()) {
            List<Coordenador> coordenadores = coordenadorRepository.findAllById(dto.getCoordenadorIds());
            coordenadores.forEach(comunicado::addDestinatarioCoordenador);
            allDestinatarios.addAll(coordenadores);
        }
        if (dto.getSupervisorIds() != null && !dto.getSupervisorIds().isEmpty()) {
            List<Supervisor> supervisores = supervisorRepository.findAllById(dto.getSupervisorIds());
            supervisores.forEach(comunicado::addDestinatarioSupervisor);
            allDestinatarios.addAll(supervisores);
        }
        if (dto.getTecnicoIds() != null && !dto.getTecnicoIds().isEmpty()) {
            List<Tecnico> tecnicos = tecnicoRepository.findAllById(dto.getTecnicoIds());
            tecnicos.forEach(comunicado::addDestinatarioTecnico);
            allDestinatarios.addAll(tecnicos);
        }

        Comunicado savedComunicado = comunicadoRepository.save(comunicado);

        // **NOVO: Crie ComunicadoStatusPorUsuario para cada destinatário**
        for (Object dest : allDestinatarios) {
            ComunicadoStatus status = new ComunicadoStatus();
            status.setComunicado(savedComunicado);
            if (dest instanceof Atleta) status.setAtleta((Atleta) dest);
            else if (dest instanceof Coordenador) status.setCoordenador((Coordenador) dest);
            else if (dest instanceof Supervisor) status.setSupervisor((Supervisor) dest);
            else if (dest instanceof Tecnico) status.setTecnico((Tecnico) dest);
            status.setOcultado(false); // Por padrão, não está oculto ao ser enviado
            comunicadoStatusPorUsuarioRepository.save(status);
        }

        // Forçar a inicialização das coleções lazy para o DTO (boa prática para o retorno)
        savedComunicado.getDestinatariosAtletas().size();
        savedComunicado.getDestinatariosCoordenadores().size();
        savedComunicado.getDestinatariosSupervisores().size();
        savedComunicado.getDestinatariosTecnicos().size();
        if(savedComunicado.getRemetenteCoordenador() != null) savedComunicado.getRemetenteCoordenador().getNome();
        if(savedComunicado.getRemetenteSupervisor() != null) savedComunicado.getRemetenteSupervisor().getNome();
        if(savedComunicado.getRemetenteTecnico() != null) savedComunicado.getRemetenteTecnico().getNome();

        return convertToDto(savedComunicado);
    }

    @Transactional
    public ComunicadoResponse atualizarComunicado(Long id, ComunicadoRequest dto) {
        Comunicado comunicado = comunicadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado com ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        // **Verificação de Permissão para Edição: APENAS O REMETENTE PODE EDITAR**
        boolean isRemetente = (loggedInUser instanceof Coordenador && comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(((Coordenador) loggedInUser).getId())) ||
                (loggedInUser instanceof Supervisor && comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(((Supervisor) loggedInUser).getId())) ||
                (loggedInUser instanceof Tecnico && comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(((Tecnico) loggedInUser).getId()));

        if (!isRemetente) {
            throw new SecurityException("Você não tem permissão para editar este comunicado.");
        }

        comunicado.setAssunto(dto.getAssunto());
        comunicado.setMensagem(dto.getMensagem());
        comunicado.setData(dto.getData() != null ? dto.getData() : comunicado.getData());

        // Limpa destinatários existentes antes de adicionar os novos
        comunicado.getDestinatariosAtletas().clear();
        comunicado.getDestinatariosCoordenadores().clear();
        comunicado.getDestinatariosSupervisores().clear();
        comunicado.getDestinatariosTecnicos().clear();

        List<Object> allNewDestinatarios = new ArrayList<>(); // Para rastrear os novos destinatários para status

        if (dto.getAtletasIds() != null && !dto.getAtletasIds().isEmpty()) {
            List<Atleta> atletas = atletaRepository.findAllById(dto.getAtletasIds());
            atletas.forEach(comunicado::addDestinatarioAtleta);
            allNewDestinatarios.addAll(atletas);
        }
        if (dto.getCoordenadorIds() != null && !dto.getCoordenadorIds().isEmpty()) {
            List<Coordenador> coordenadores = coordenadorRepository.findAllById(dto.getCoordenadorIds());
            coordenadores.forEach(comunicado::addDestinatarioCoordenador);
            allNewDestinatarios.addAll(coordenadores);
        }
        if (dto.getSupervisorIds() != null && !dto.getSupervisorIds().isEmpty()) {
            List<Supervisor> supervisores = supervisorRepository.findAllById(dto.getSupervisorIds());
            supervisores.forEach(comunicado::addDestinatarioSupervisor);
            allNewDestinatarios.addAll(supervisores);
        }
        if (dto.getTecnicoIds() != null && !dto.getTecnicoIds().isEmpty()) {
            List<Tecnico> tecnicos = tecnicoRepository.findAllById(dto.getTecnicoIds());
            tecnicos.forEach(comunicado::addDestinatarioTecnico);
            allNewDestinatarios.addAll(tecnicos);
        }

        Comunicado updatedComunicado = comunicadoRepository.save(comunicado);



        List<ComunicadoStatus> existingStatuses = comunicadoStatusPorUsuarioRepository.findByComunicado(updatedComunicado);
        Set<Long> currentDestinatarioIds = new HashSet<>();
        allNewDestinatarios.forEach(d -> {
            if (d instanceof Atleta) currentDestinatarioIds.add(((Atleta) d).getId());
            else if (d instanceof Coordenador) currentDestinatarioIds.add(((Coordenador) d).getId());
            else if (d instanceof Supervisor) currentDestinatarioIds.add(((Supervisor) d).getId());
            else if (d instanceof Tecnico) currentDestinatarioIds.add(((Tecnico) d).getId());
        });

        // Remover status de usuários que não são mais destinatários
        existingStatuses.stream()
                .filter(status -> status.getAssociatedUserId() != null && !currentDestinatarioIds.contains(status.getAssociatedUserId()))
                .forEach(comunicadoStatusPorUsuarioRepository::delete);

        // Adicionar status para novos destinatários (ou reativar se existia e estava oculto)
        for (Object newDest : allNewDestinatarios) {
            Optional<ComunicadoStatus> existingStatus = Optional.empty();
            if (newDest instanceof Atleta) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndAtletaId(updatedComunicado, ((Atleta) newDest).getId());
            } else if (newDest instanceof Coordenador) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndCoordenadorId(updatedComunicado, ((Coordenador) newDest).getId());
            } else if (newDest instanceof Supervisor) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndSupervisorId(updatedComunicado, ((Supervisor) newDest).getId());
            } else if (newDest instanceof Tecnico) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndTecnicoId(updatedComunicado, ((Tecnico) newDest).getId());
            }

            if (existingStatus.isEmpty()) {
                ComunicadoStatus newStatus = new ComunicadoStatus();
                newStatus.setComunicado(updatedComunicado);
                if (newDest instanceof Atleta) newStatus.setAtleta((Atleta) newDest);
                else if (newDest instanceof Coordenador) newStatus.setCoordenador((Coordenador) newDest);
                else if (newDest instanceof Supervisor) newStatus.setSupervisor((Supervisor) newDest);
                else if (newDest instanceof Tecnico) newStatus.setTecnico((Tecnico) newDest);
                newStatus.setOcultado(false); // Garante que o status está "não oculto" para novos/reativados
                comunicadoStatusPorUsuarioRepository.save(newStatus);
            } else if (existingStatus.get().isOcultado()) {
                // Se o status existia e estava oculto, mas o usuário foi adicionado novamente, "reative"
                ComunicadoStatus statusToReactivate = existingStatus.get();
                statusToReactivate.setOcultado(false);
                comunicadoStatusPorUsuarioRepository.save(statusToReactivate);
            }
        }


        // Forçar a inicialização das coleções lazy para o DTO
        updatedComunicado.getDestinatariosAtletas().size();
        updatedComunicado.getDestinatariosCoordenadores().size();
        updatedComunicado.getDestinatariosSupervisores().size();
        updatedComunicado.getDestinatariosTecnicos().size();
        if(updatedComunicado.getRemetenteCoordenador() != null) updatedComunicado.getRemetenteCoordenador().getNome();
        if(updatedComunicado.getRemetenteSupervisor() != null) updatedComunicado.getRemetenteSupervisor().getNome();
        if(updatedComunicado.getRemetenteTecnico() != null) updatedComunicado.getRemetenteTecnico().getNome();

        return convertToDto(updatedComunicado);
    }

    @Transactional(readOnly = true)
    public List<ComunicadoResponse> buscarTodosComunicados() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Set<Comunicado> comunicadosDoUsuario = new HashSet<>();

        Object loggedInUser = getLoggedInUserEntity(username, authorities);
        if (loggedInUser == null) {
            return Collections.emptyList(); // Usuário não autenticado ou sem tipo de usuário
        }

        Long loggedInUserId = null;
        if (loggedInUser instanceof Atleta) loggedInUserId = ((Atleta) loggedInUser).getId();
        else if (loggedInUser instanceof Coordenador) loggedInUserId = ((Coordenador) loggedInUser).getId();
        else if (loggedInUser instanceof Supervisor) loggedInUserId = ((Supervisor) loggedInUser).getId();
        else if (loggedInUser instanceof Tecnico) loggedInUserId = ((Tecnico) loggedInUser).getId();

        // Buscar comunicados enviados pelo usuário logado
        if (loggedInUser instanceof Coordenador) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteCoordenadorWithDetails((Coordenador) loggedInUser));
        } else if (loggedInUser instanceof Supervisor) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteSupervisorWithDetails((Supervisor) loggedInUser));
        } else if (loggedInUser instanceof Tecnico) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteTecnicoWithDetails((Tecnico) loggedInUser));
        }

        // Buscar comunicados onde o usuário logado é destinatário, e que NÃO ESTEJAM OCULTOS
        // Usaremos os novos métodos do repositório
        if (loggedInUser instanceof Atleta) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioAtletaIdAndNotOcultado(((Atleta) loggedInUser).getId()));
        } else if (loggedInUser instanceof Coordenador) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioCoordenadorIdAndNotOcultado(((Coordenador) loggedInUser).getId()));
        } else if (loggedInUser instanceof Supervisor) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioSupervisorIdAndNotOcultado(((Supervisor) loggedInUser).getId()));
        } else if (loggedInUser instanceof Tecnico) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioTecnicoIdAndNotOcultado(((Tecnico) loggedInUser).getId()));
        }

        // Se for ADMIN, pode ver todos os comunicados (independentemente do status de ocultação individual)
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findAllWithAllDetails());
        }

        return comunicadosDoUsuario.stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(ComunicadoResponse::getDataEnvio, Comparator.reverseOrder())) // Ordenar por data de envio (mais recente primeiro)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ComunicadoResponse> buscarComunicadoPorId(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        if (loggedInUser == null) {
            return Optional.empty(); // Usuário não autenticado
        }

        Comunicado comunicado = comunicadoRepository.findByIdWithAllDetails(id)
                .orElse(null); // Retorna null se não encontrar

        if (comunicado == null) {
            return Optional.empty();
        }

        boolean podeVer = false;
        Long loggedInUserId = null;

        if (loggedInUser instanceof Atleta) loggedInUserId = ((Atleta) loggedInUser).getId();
        else if (loggedInUser instanceof Coordenador) loggedInUserId = ((Coordenador) loggedInUser).getId();
        else if (loggedInUser instanceof Supervisor) loggedInUserId = ((Supervisor) loggedInUser).getId();
        else if (loggedInUser instanceof Tecnico) loggedInUserId = ((Tecnico) loggedInUser).getId();


        // 1. O usuário é o remetente?
        if ((loggedInUser instanceof Coordenador && comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof Supervisor && comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof Tecnico && comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(loggedInUserId))) {
            podeVer = true;
        }

        // 2. O usuário é um destinatário e o comunicado NÃO está oculto para ele?
        if (!podeVer) { // Se já pode ver como remetente, não precisa checar como destinatário
            Optional<ComunicadoStatus> statusOptional = Optional.empty();
            if (loggedInUser instanceof Atleta) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndAtletaId(comunicado, loggedInUserId);
            } else if (loggedInUser instanceof Coordenador) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndCoordenadorId(comunicado, loggedInUserId);
            } else if (loggedInUser instanceof Supervisor) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndSupervisorId(comunicado, loggedInUserId);
            } else if (loggedInUser instanceof Tecnico) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndTecnicoId(comunicado, loggedInUserId);
            }

            // Pode ver se é destinatário E o status não está oculto (ou não há status, o que implica não oculto)
            if (statusOptional.isPresent()) {
                podeVer = !statusOptional.get().isOcultado();
            } else {
                // Se não há um registro de status, significa que o usuário não é um destinatário OU não foi adicionado ao status
                // Se a lógica é que SÓ destinatários podem ver (além do remetente e admin), então esse else significaria que não pode ver
                // Se você quer que usuários que são destinatários vejam mesmo sem registro na tabela de status (apenas por serem links),
                // você precisaria de uma checagem adicional aqui (e sua query de busca por destinatário já faria isso).
                // Por agora, vamos manter o filtro rigoroso pelo status.
            }
        }

        // 3. O usuário é ADMIN?
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            podeVer = true;
        }

        if (podeVer) {
            return Optional.of(convertToDto(comunicado));
        } else {
            return Optional.empty(); // Não tem permissão para ver ou está oculto
        }
    }

    // **NOVO MÉTODO: Para ocultar um comunicado da sessão do usuário logado**
    @Transactional
    public void ocultarComunicadoParaUsuario(Long comunicadoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());

        Object loggedInUser = getLoggedInUserEntity(username, authorities);
        if (loggedInUser == null) {
            throw new RuntimeException("Usuário autenticado não encontrado.");
        }

        Comunicado comunicado = comunicadoRepository.findById(comunicadoId)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado."));

        Optional<ComunicadoStatus> statusOptional = Optional.empty();
        Long loggedInUserId = null;

        if (loggedInUser instanceof Atleta) {
            loggedInUserId = ((Atleta) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndAtletaId(comunicado, loggedInUserId);
        } else if (loggedInUser instanceof Coordenador) {
            loggedInUserId = ((Coordenador) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndCoordenadorId(comunicado, loggedInUserId);
        } else if (loggedInUser instanceof Supervisor) {
            loggedInUserId = ((Supervisor) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndSupervisorId(comunicado, loggedInUserId);
        } else if (loggedInUser instanceof Tecnico) {
            loggedInUserId = ((Tecnico) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndTecnicoId(comunicado, loggedInUserId);
        }

        // O usuário logado é o remetente? Se sim, ele não pode "ocultar" da sua própria sessão como destinatário.
        // Ele teria a opção de "deletar permanentemente" (se permitido pelo Admin).
        boolean isRemetente = (comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(loggedInUserId)) ||
                (comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(loggedInUserId)) ||
                (comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(loggedInUserId));

        if (isRemetente) {
            throw new SecurityException("Remetentes não podem 'ocultar' comunicados da sua sessão; eles podem excluí-los permanentemente (se tiverem permissão).");
        }


        if (statusOptional.isPresent()) {
            ComunicadoStatus status = statusOptional.get();
            if (status.isOcultado()) {
                throw new RuntimeException("Comunicado já está oculto para este usuário.");
            }
            status.setOcultado(true); // Marca como ocultado
            comunicadoStatusPorUsuarioRepository.save(status);
        } else {
            // Se não encontrou o status, significa que o usuário não é um destinatário direto
            // (ou um status para ele ainda não foi criado, o que não deveria acontecer se a criação for correta).
            // Para "ocultar da minha sessão", o usuário precisa ser um destinatário.
            throw new RuntimeException("Você não é um destinatário deste comunicado ou o status não pôde ser encontrado.");
        }
    }

    // **MÉTODO MODIFICADO: Deletar Comunicado Permanentemente (APENAS REMETENTE OU ADMIN)**
    @Transactional
    public void deletarComunicadoPermanentemente(Long id) { // Renomeado para clareza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        if (loggedInUser == null) {
            throw new RuntimeException("Usuário autenticado não encontrado.");
        }

        Comunicado comunicado = comunicadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado com ID: " + id));

        boolean podeDeletarPermanentemente = false;
        Long loggedInUserId = null;

        if (loggedInUser instanceof Coordenador) loggedInUserId = ((Coordenador) loggedInUser).getId();
        else if (loggedInUser instanceof Supervisor) loggedInUserId = ((Supervisor) loggedInUser).getId();
        else if (loggedInUser instanceof Tecnico) loggedInUserId = ((Tecnico) loggedInUser).getId();

        // Apenas o remetente pode deletar permanentemente
        if ((loggedInUser instanceof Coordenador && comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof Supervisor && comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof Tecnico && comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(loggedInUserId))) {
            podeDeletarPermanentemente = true;
        }

        // Ou se for ADMIN
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            podeDeletarPermanentemente = true;
        }

        if (!podeDeletarPermanentemente) {
            throw new SecurityException("Você não tem permissão para deletar este comunicado permanentemente.");
        }

        // Antes de deletar o comunicado, deletar todas as entradas de status associadas
        comunicadoStatusPorUsuarioRepository.deleteAll(comunicadoStatusPorUsuarioRepository.findByComunicado(comunicado));

        comunicadoRepository.delete(comunicado);
    }
}
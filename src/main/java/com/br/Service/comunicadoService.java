package com.br.Service;

import com.br.Entity.*;
import com.br.Enums.role;
import com.br.Repository.*;
import com.br.Request.comunicadoRequest;
import com.br.Response.comunicadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections; // Importe este
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator; // Importe este
import java.util.stream.Collectors;

@Service
public class comunicadoService {

    @Autowired
    private comunicadoRepository comunicadoRepository;

    @Autowired
    private atletaRepository atletaRepository;

    @Autowired
    private coordenadorRepository coordenadorRepository;

    @Autowired
    private supervisorRepository supervisorRepository;

    @Autowired
    private tecnicoRepository tecnicoRepository;

    @Autowired // NOVO: Injete o novo repositório
    private comunicadoStatusRepository comunicadoStatusPorUsuarioRepository;


    @Transactional(readOnly = true)
    protected Object getLoggedInUserEntity(String username, Set<GrantedAuthority> authorities) {


        if (authorities.stream().anyMatch(a -> a.getAuthority().equals( role.COORDENADOR.name()))) {
            return coordenadorRepository.findByEmail(username).orElse(null);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals( role.SUPERVISOR.name()))) {
            return supervisorRepository.findByEmail(username).orElse(null);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals( role.TECNICO.name()))) {
            return tecnicoRepository.findByEmail(username).orElse(null);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals( role.ATLETA.name()))) {
            return atletaRepository.findByEmail(username).orElse(null);
        }
        return null;
    }


    private comunicadoResponse convertToDto(comunicado comunicado) {
        comunicadoResponse dto = new comunicadoResponse();
        dto.setId(comunicado.getId());
        dto.setAssunto(comunicado.getAssunto());
        dto.setMensagem(comunicado.getMensagem());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dto.setDataEnvio((comunicado.getData().format(formatter))); // Use DataEnvio para consistência com o frontend

        List<comunicadoResponse.DestinatarioDTO> destinatarios = new ArrayList<>();


        if (comunicado.getDestinatariosAtletas() != null) {
            comunicado.getDestinatariosAtletas().forEach(atleta ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(atleta.getId(), atleta.getNome(), role.ATLETA.name()))
            );
        }
        // Adiciona destinatários Coordenadores
        if (comunicado.getDestinatariosCoordenadores() != null) {
            comunicado.getDestinatariosCoordenadores().forEach(coord ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(coord.getId(), coord.getNome(), role.COORDENADOR.name()))
            );
        }
        // Adiciona destinatários Supervisores
        if (comunicado.getDestinatariosSupervisores() != null) {
            comunicado.getDestinatariosSupervisores().forEach(superv ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(superv.getId(), superv.getNome(), role.SUPERVISOR.name()))
            );
        }
        // Adiciona destinatários Técnicos
        if (comunicado.getDestinatariosTecnicos() != null) {
            comunicado.getDestinatariosTecnicos().forEach(tec ->
                    destinatarios.add(new comunicadoResponse.DestinatarioDTO(tec.getId(), tec.getNome(), role.TECNICO.name()))
            );
        }

        dto.setDestinatarios(destinatarios);

        // Lógica para mapear o remetente
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
    public comunicadoResponse criarComunicado(comunicadoRequest dto) {

        comunicado comunicado = new comunicado();
        comunicado.setAssunto(dto.getAssunto());
        comunicado.setMensagem(dto.getMensagem());
        comunicado.setData(dto.getData() != null ? dto.getData() : LocalDate.now()); // Data de envio

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());

        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        if (loggedInUser instanceof coordenador) {
            comunicado.setRemetenteCoordenador((coordenador) loggedInUser);
        } else if (loggedInUser instanceof supervisor) {
            comunicado.setRemetenteSupervisor((supervisor) loggedInUser);
        } else if (loggedInUser instanceof tecnico) {
            comunicado.setRemetenteTecnico((tecnico) loggedInUser);
        } else {
            throw new RuntimeException("Usuário autenticado (" + username + ") não tem permissão para criar comunicados.");
        }

        List<Object> allDestinatarios = new ArrayList<>(); // Lista para armazenar todos os objetos destinatários

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

        comunicado savedComunicado = comunicadoRepository.save(comunicado);

        // **NOVO: Crie ComunicadoStatusPorUsuario para cada destinatário**
        for (Object dest : allDestinatarios) {
            comunicadoStatus status = new comunicadoStatus();
            status.setComunicado(savedComunicado);
            if (dest instanceof atleta) status.setAtleta((atleta) dest);
            else if (dest instanceof coordenador) status.setCoordenador((coordenador) dest);
            else if (dest instanceof supervisor) status.setSupervisor((supervisor) dest);
            else if (dest instanceof tecnico) status.setTecnico((tecnico) dest);
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
    public comunicadoResponse atualizarComunicado(Long id, comunicadoRequest dto) {
        comunicado comunicado = comunicadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado com ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        // **Verificação de Permissão para Edição: APENAS O REMETENTE PODE EDITAR**
        boolean isRemetente = (loggedInUser instanceof coordenador && comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(((coordenador) loggedInUser).getId())) ||
                (loggedInUser instanceof supervisor && comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(((supervisor) loggedInUser).getId())) ||
                (loggedInUser instanceof tecnico && comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(((tecnico) loggedInUser).getId()));

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
            List<atleta> atletas = atletaRepository.findAllById(dto.getAtletasIds());
            atletas.forEach(comunicado::addDestinatarioAtleta);
            allNewDestinatarios.addAll(atletas);
        }
        if (dto.getCoordenadorIds() != null && !dto.getCoordenadorIds().isEmpty()) {
            List<coordenador> coordenadores = coordenadorRepository.findAllById(dto.getCoordenadorIds());
            coordenadores.forEach(comunicado::addDestinatarioCoordenador);
            allNewDestinatarios.addAll(coordenadores);
        }
        if (dto.getSupervisorIds() != null && !dto.getSupervisorIds().isEmpty()) {
            List<supervisor> supervisores = supervisorRepository.findAllById(dto.getSupervisorIds());
            supervisores.forEach(comunicado::addDestinatarioSupervisor);
            allNewDestinatarios.addAll(supervisores);
        }
        if (dto.getTecnicoIds() != null && !dto.getTecnicoIds().isEmpty()) {
            List<tecnico> tecnicos = tecnicoRepository.findAllById(dto.getTecnicoIds());
            tecnicos.forEach(comunicado::addDestinatarioTecnico);
            allNewDestinatarios.addAll(tecnicos);
        }

        comunicado updatedComunicado = comunicadoRepository.save(comunicado);



        List<comunicadoStatus> existingStatuses = comunicadoStatusPorUsuarioRepository.findByComunicado(updatedComunicado);
        Set<Long> currentDestinatarioIds = new HashSet<>();
        allNewDestinatarios.forEach(d -> {
            if (d instanceof atleta) currentDestinatarioIds.add(((atleta) d).getId());
            else if (d instanceof coordenador) currentDestinatarioIds.add(((coordenador) d).getId());
            else if (d instanceof supervisor) currentDestinatarioIds.add(((supervisor) d).getId());
            else if (d instanceof tecnico) currentDestinatarioIds.add(((tecnico) d).getId());
        });

        // Remover status de usuários que não são mais destinatários
        existingStatuses.stream()
                .filter(status -> status.getAssociatedUserId() != null && !currentDestinatarioIds.contains(status.getAssociatedUserId()))
                .forEach(comunicadoStatusPorUsuarioRepository::delete);

        // Adicionar status para novos destinatários (ou reativar se existia e estava oculto)
        for (Object newDest : allNewDestinatarios) {
            Optional<comunicadoStatus> existingStatus = Optional.empty();
            if (newDest instanceof atleta) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndAtletaId(updatedComunicado, ((atleta) newDest).getId());
            } else if (newDest instanceof coordenador) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndCoordenadorId(updatedComunicado, ((coordenador) newDest).getId());
            } else if (newDest instanceof supervisor) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndSupervisorId(updatedComunicado, ((supervisor) newDest).getId());
            } else if (newDest instanceof tecnico) {
                existingStatus = comunicadoStatusPorUsuarioRepository.findByComunicadoAndTecnicoId(updatedComunicado, ((tecnico) newDest).getId());
            }

            if (existingStatus.isEmpty()) {
                comunicadoStatus newStatus = new comunicadoStatus();
                newStatus.setComunicado(updatedComunicado);
                if (newDest instanceof atleta) newStatus.setAtleta((atleta) newDest);
                else if (newDest instanceof coordenador) newStatus.setCoordenador((coordenador) newDest);
                else if (newDest instanceof supervisor) newStatus.setSupervisor((supervisor) newDest);
                else if (newDest instanceof tecnico) newStatus.setTecnico((tecnico) newDest);
                newStatus.setOcultado(false); // Garante que o status está "não oculto" para novos/reativados
                comunicadoStatusPorUsuarioRepository.save(newStatus);
            } else if (existingStatus.get().isOcultado()) {
                // Se o status existia e estava oculto, mas o usuário foi adicionado novamente, "reative"
                comunicadoStatus statusToReactivate = existingStatus.get();
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
    public List<comunicadoResponse> buscarTodosComunicados() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Set<comunicado> comunicadosDoUsuario = new HashSet<>();

        Object loggedInUser = getLoggedInUserEntity(username, authorities);
        if (loggedInUser == null) {
            return Collections.emptyList(); // Usuário não autenticado ou sem tipo de usuário
        }

        Long loggedInUserId = null;
        if (loggedInUser instanceof atleta) loggedInUserId = ((atleta) loggedInUser).getId();
        else if (loggedInUser instanceof coordenador) loggedInUserId = ((coordenador) loggedInUser).getId();
        else if (loggedInUser instanceof supervisor) loggedInUserId = ((supervisor) loggedInUser).getId();
        else if (loggedInUser instanceof tecnico) loggedInUserId = ((tecnico) loggedInUser).getId();

        // Buscar comunicados enviados pelo usuário logado
        if (loggedInUser instanceof coordenador) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteCoordenadorWithDetails((coordenador) loggedInUser));
        } else if (loggedInUser instanceof supervisor) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteSupervisorWithDetails((supervisor) loggedInUser));
        } else if (loggedInUser instanceof tecnico) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findByRemetenteTecnicoWithDetails((tecnico) loggedInUser));
        }

        // Buscar comunicados onde o usuário logado é destinatário, e que NÃO ESTEJAM OCULTOS
        // Usaremos os novos métodos do repositório
        if (loggedInUser instanceof atleta) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioAtletaIdAndNotOcultado(((atleta) loggedInUser).getId()));
        } else if (loggedInUser instanceof coordenador) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioCoordenadorIdAndNotOcultado(((coordenador) loggedInUser).getId()));
        } else if (loggedInUser instanceof supervisor) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioSupervisorIdAndNotOcultado(((supervisor) loggedInUser).getId()));
        } else if (loggedInUser instanceof tecnico) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findComunicadosByDestinatarioTecnicoIdAndNotOcultado(((tecnico) loggedInUser).getId()));
        }

        // Se for ADMIN, pode ver todos os comunicados (independentemente do status de ocultação individual)
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            comunicadosDoUsuario.addAll(comunicadoRepository.findAllWithAllDetails());
        }

        return comunicadosDoUsuario.stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(comunicadoResponse::getDataEnvio, Comparator.reverseOrder())) // Ordenar por data de envio (mais recente primeiro)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<comunicadoResponse> buscarComunicadoPorId(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        Object loggedInUser = getLoggedInUserEntity(username, authorities);

        if (loggedInUser == null) {
            return Optional.empty(); // Usuário não autenticado
        }

        comunicado comunicado = comunicadoRepository.findByIdWithAllDetails(id)
                .orElse(null); // Retorna null se não encontrar

        if (comunicado == null) {
            return Optional.empty();
        }

        boolean podeVer = false;
        Long loggedInUserId = null;

        if (loggedInUser instanceof atleta) loggedInUserId = ((atleta) loggedInUser).getId();
        else if (loggedInUser instanceof coordenador) loggedInUserId = ((coordenador) loggedInUser).getId();
        else if (loggedInUser instanceof supervisor) loggedInUserId = ((supervisor) loggedInUser).getId();
        else if (loggedInUser instanceof tecnico) loggedInUserId = ((tecnico) loggedInUser).getId();


        // 1. O usuário é o remetente?
        if ((loggedInUser instanceof coordenador && comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof supervisor && comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof tecnico && comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(loggedInUserId))) {
            podeVer = true;
        }

        // 2. O usuário é um destinatário e o comunicado NÃO está oculto para ele?
        if (!podeVer) { // Se já pode ver como remetente, não precisa checar como destinatário
            Optional<comunicadoStatus> statusOptional = Optional.empty();
            if (loggedInUser instanceof atleta) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndAtletaId(comunicado, loggedInUserId);
            } else if (loggedInUser instanceof coordenador) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndCoordenadorId(comunicado, loggedInUserId);
            } else if (loggedInUser instanceof supervisor) {
                statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndSupervisorId(comunicado, loggedInUserId);
            } else if (loggedInUser instanceof tecnico) {
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

        comunicado comunicado = comunicadoRepository.findById(comunicadoId)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado."));

        Optional<comunicadoStatus> statusOptional = Optional.empty();
        Long loggedInUserId = null;

        if (loggedInUser instanceof atleta) {
            loggedInUserId = ((atleta) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndAtletaId(comunicado, loggedInUserId);
        } else if (loggedInUser instanceof coordenador) {
            loggedInUserId = ((coordenador) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndCoordenadorId(comunicado, loggedInUserId);
        } else if (loggedInUser instanceof supervisor) {
            loggedInUserId = ((supervisor) loggedInUser).getId();
            statusOptional = comunicadoStatusPorUsuarioRepository.findByComunicadoAndSupervisorId(comunicado, loggedInUserId);
        } else if (loggedInUser instanceof tecnico) {
            loggedInUserId = ((tecnico) loggedInUser).getId();
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
            comunicadoStatus status = statusOptional.get();
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

        comunicado comunicado = comunicadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrado com ID: " + id));

        boolean podeDeletarPermanentemente = false;
        Long loggedInUserId = null;

        if (loggedInUser instanceof coordenador) loggedInUserId = ((coordenador) loggedInUser).getId();
        else if (loggedInUser instanceof supervisor) loggedInUserId = ((supervisor) loggedInUser).getId();
        else if (loggedInUser instanceof tecnico) loggedInUserId = ((tecnico) loggedInUser).getId();

        // Apenas o remetente pode deletar permanentemente
        if ((loggedInUser instanceof coordenador && comunicado.getRemetenteCoordenador() != null && comunicado.getRemetenteCoordenador().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof supervisor && comunicado.getRemetenteSupervisor() != null && comunicado.getRemetenteSupervisor().getId().equals(loggedInUserId)) ||
                (loggedInUser instanceof tecnico && comunicado.getRemetenteTecnico() != null && comunicado.getRemetenteTecnico().getId().equals(loggedInUserId))) {
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
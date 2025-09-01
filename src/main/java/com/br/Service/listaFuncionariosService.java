package com.br.Service;

import com.br.Enums.role;
import com.br.Repository.coordenadorRepository;
import com.br.Repository.supervisorRepository;
import com.br.Repository.tecnicoRepository;
import com.br.Response.funcionarioListagemResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class listaFuncionariosService {

    private final supervisorRepository supervisorRepository;
    private final coordenadorRepository coordenadorRepository;
    private final tecnicoRepository tecnicoRepository;

    @Autowired
    public listaFuncionariosService(supervisorRepository supervisorRepository,
                                    coordenadorRepository coordenadorRepository,
                                    tecnicoRepository tecnicoRepository) {
        this.supervisorRepository = supervisorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Transactional
    public  void atualizarFuncionario(funcionarioListagemResponse dto) {
        switch (dto.getRoles()) {
            case SUPERVISOR:
                supervisorRepository.findById(dto.getId()).ifPresent(supervisor -> {
                    supervisor.setNome(dto.getNome());
                    supervisor.setEmail(dto.getEmail());
                    supervisor.setDataNascimento(dto.getDataNascimento());
                    supervisor.setCpf(dto.getCpf());
                    supervisor.setTelefone(dto.getTelefone());
                    supervisorRepository.save(supervisor);
                });
                break;
            case COORDENADOR:
                coordenadorRepository.findById(dto.getId()).ifPresent(coordenador -> {
                    coordenador.setNome(dto.getNome());
                    coordenador.setEmail(dto.getEmail());
                    coordenador.setDataNascimento(dto.getDataNascimento());
                    coordenador.setCpf(dto.getCpf());
                    coordenador.setTelefone(dto.getTelefone());
                    coordenadorRepository.save(coordenador);
                });
                break;
            case TECNICO:
                tecnicoRepository.findById(dto.getId()).ifPresent(tecnico -> {
                    tecnico.setNome(dto.getNome());
                    tecnico.setEmail(dto.getEmail());
                    tecnico.setDataNascimento(dto.getDataNascimento());
                    tecnico.setCpf(dto.getCpf());
                    tecnico.setTelefone(dto.getTelefone());
                    tecnicoRepository.save(tecnico);
                });
                break;
            default:
                throw new IllegalArgumentException("Tipo de funcionário não suportado: " + dto.getRoles());
        }
    }
    @Transactional
    public void deletarFuncionario(Long id, role role) {
        switch (role) {
            case SUPERVISOR:
                supervisorRepository.deleteById(id);
                break;
            case COORDENADOR:
                coordenadorRepository.deleteById(id);
                break;
            case TECNICO:
                tecnicoRepository.deleteById(id);
                break;
            default:
                throw new IllegalArgumentException("Tipo de funcionário não suportado: " + role);
        }
    }
}
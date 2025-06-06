package com.br.Service;

import com.br.Entity.Coordenador;
import com.br.Entity.Super; // Importe a Superclass
import com.br.Entity.Supervisor;
import com.br.Entity.Tecnico;
import com.br.Enums.Role; // Importe seu Enum Role
import com.br.Repository.CoordenadorRepository;
import com.br.Repository.SupervisorRepository;
import com.br.Repository.TecnicoRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Adicione esta anotação

@Service
public class CadastroFuncionariosService {
    private final CoordenadorRepository coordenadorRepository;
    private final SupervisorRepository supervisorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final PasswordEncoder passwordEncoder; // Injetar PasswordEncoder

    public CadastroFuncionariosService(CoordenadorRepository coordenadorRepository,
                                       SupervisorRepository supervisorRepository,
                                       TecnicoRepository tecnicoRepository,
                                       PasswordEncoder passwordEncoder) { // Adicionar ao construtor
        this.coordenadorRepository = coordenadorRepository;
        this.supervisorRepository = supervisorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.passwordEncoder = passwordEncoder; // Atribuir
    }

    // Método auxiliar para pré-processar as entidades baseadas em Super
    private <T extends Super> T preprocessEmployee(T employee, Role defaultRole) {
        // Codificar a senha
        if (employee.getSenha() != null && !employee.getSenha().isEmpty()) {
            employee.setSenha(passwordEncoder.encode(employee.getSenha()));
        } else {
            throw new IllegalArgumentException("A senha é obrigatória para o cadastro.");
        }


        if (employee.getRoles() == null) {
            employee.setRoles(defaultRole);
        }
        return employee;
    }

    @Transactional
    public Tecnico cadastrarTecnico(Tecnico tecnico) {
        Tecnico preprocessedTecnico = preprocessEmployee(tecnico, Role.TECNICO);
        return tecnicoRepository.save(preprocessedTecnico);
    }

    @Transactional
    public Supervisor cadastrarSupervisor(Supervisor supervisor) {
        Supervisor preprocessedSupervisor = preprocessEmployee(supervisor, Role.SUPERVISOR);
        return supervisorRepository.save(preprocessedSupervisor);
    }

    @Transactional
    public Coordenador cadastrarCoordenador(Coordenador coordenador) {
        Coordenador preprocessedCoordenador = preprocessEmployee(coordenador, Role.COORDENADOR);
        return coordenadorRepository.save(preprocessedCoordenador);
    }
}
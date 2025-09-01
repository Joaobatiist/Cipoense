package com.br.Service;

import com.br.Entity.coordenador;
import com.br.Entity.Super; // Importe a Superclass
import com.br.Entity.supervisor;
import com.br.Entity.tecnico;
import com.br.Enums.role; // Importe seu Enum Role
import com.br.Repository.coordenadorRepository;
import com.br.Repository.supervisorRepository;
import com.br.Repository.tecnicoRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Adicione esta anotação

@Service
public class cadastroFuncionariosService {
    private final coordenadorRepository coordenadorRepository;
    private final supervisorRepository supervisorRepository;
    private final tecnicoRepository tecnicoRepository;
    private final PasswordEncoder passwordEncoder; // Injetar PasswordEncoder

    public cadastroFuncionariosService(coordenadorRepository coordenadorRepository,
                                       supervisorRepository supervisorRepository,
                                       tecnicoRepository tecnicoRepository,
                                       PasswordEncoder passwordEncoder) { // Adicionar ao construtor
        this.coordenadorRepository = coordenadorRepository;
        this.supervisorRepository = supervisorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.passwordEncoder = passwordEncoder; // Atribuir
    }

    // Método auxiliar para pré-processar as entidades baseadas em Super
    private <T extends Super> T preprocessEmployee(T employee, role defaultRole) {
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
    public tecnico cadastrarTecnico(tecnico tecnico) {
        tecnico preprocessedTecnico = preprocessEmployee(tecnico, role.TECNICO);
        return tecnicoRepository.save(preprocessedTecnico);
    }

    @Transactional
    public supervisor cadastrarSupervisor(supervisor supervisor) {
        supervisor preprocessedSupervisor = preprocessEmployee(supervisor, role.SUPERVISOR);
        return supervisorRepository.save(preprocessedSupervisor);
    }

    @Transactional
    public coordenador cadastrarCoordenador(coordenador coordenador) {
        coordenador preprocessedCoordenador = preprocessEmployee(coordenador, role.COORDENADOR);
        return coordenadorRepository.save(preprocessedCoordenador);
    }
}
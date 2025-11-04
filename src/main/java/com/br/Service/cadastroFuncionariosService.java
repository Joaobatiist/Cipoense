package com.br.Service;

import com.br.Entity.funcionario;
import com.br.Enums.role; // Importe seu Enum Role
import com.br.Repository.funcionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class cadastroFuncionariosService {
    private final funcionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public cadastroFuncionariosService(funcionarioRepository funcionarioRepository,
                                       PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método auxiliar unificado para pré-processar o funcionário
    private funcionario preprocessFuncionario(funcionario novoFuncionario) {
        // 1. Validação da Role
        if (novoFuncionario.getRole() == null) {
            throw new IllegalArgumentException("A Role (cargo) é obrigatória para o cadastro.");
        }

        // 2. Codificar a senha
        if (novoFuncionario.getSenha() != null && !novoFuncionario.getSenha().isEmpty()) {
            novoFuncionario.setSenha(passwordEncoder.encode(novoFuncionario.getSenha()));
        } else {
            throw new IllegalArgumentException("A senha é obrigatória para o cadastro.");
        }

        return novoFuncionario;
    }

    // Método único para cadastrar qualquer tipo de funcionário
    @Transactional
    public funcionario cadastrar(funcionario novoFuncionario) {
        funcionario preprocessedFuncionario = preprocessFuncionario(novoFuncionario);
        return funcionarioRepository.save(preprocessedFuncionario);
    }

    // Os métodos cadastrarTecnico, cadastrarSupervisor e cadastrarCoordenador foram removidos
}
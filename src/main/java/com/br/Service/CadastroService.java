// src/main/java/com/br/Service/CadastroService.java
package com.br.Service;

import com.br.Entity.Aluno;
import com.br.Entity.Responsavel;
import com.br.Enums.Role;
import com.br.Repository.AlunoRepository;
import com.br.Repository.ResponsavelRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CadastroService {

    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroService(AlunoRepository alunoRepository,
                           ResponsavelRepository responsavelRepository,
                           PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.responsavelRepository = responsavelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Aluno cadastrarAlunoComResponsavel(Aluno aluno) {
        if (aluno.getResponsavel() != null) {
            Responsavel responsavelSalvo = responsavelRepository.save(aluno.getResponsavel());
            aluno.setResponsavel(responsavelSalvo);
        } else {
            throw new IllegalArgumentException("Responsável é obrigatório para o cadastro do aluno.");
        }

        aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));

        if (aluno.getRoles() == null) {
            aluno.setRoles(Role.ALUNO);
        }

        return alunoRepository.save(aluno);
    }
    public List<Aluno> listarTodosAlunos() {
        return alunoRepository.findAll();
    }
}
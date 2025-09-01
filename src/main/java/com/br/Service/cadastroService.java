
package com.br.Service;

import com.br.Entity.atleta;
import com.br.Entity.responsavel;
import com.br.Enums.role;
import com.br.Repository.atletaRepository;
import com.br.Repository.responsavelRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class cadastroService {

    private final atletaRepository atletaRepository;
    private final responsavelRepository responsavelRepository;
    private final PasswordEncoder passwordEncoder;

    public cadastroService(atletaRepository atletaRepository,
                           responsavelRepository responsavelRepository,
                           PasswordEncoder passwordEncoder) {
        this.atletaRepository = atletaRepository;
        this.responsavelRepository = responsavelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public atleta cadastrarAtletaComResponsavel(atleta atleta) {
        if (atleta.getResponsavel() != null) {
            responsavel responsavelSalvo = responsavelRepository.save(atleta.getResponsavel());
            atleta.setResponsavel(responsavelSalvo);
        } else {
            throw new IllegalArgumentException("Responsável é obrigatório para o cadastro do aluno.");
        }

        atleta.setSenha(passwordEncoder.encode(atleta.getSenha()));

        if (atleta.getRoles() == null) {
            atleta.setRoles(role.ATLETA);
        }

        return atletaRepository.save(atleta);
    }
    public List<atleta> listarTodosAtletas() {
        return atletaRepository.findAll();
    }
}
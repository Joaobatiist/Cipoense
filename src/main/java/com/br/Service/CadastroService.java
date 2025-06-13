
package com.br.Service;

import com.br.Entity.Atleta;
import com.br.Entity.Responsavel;
import com.br.Enums.Role;
import com.br.Repository.AtletaRepository;
import com.br.Repository.ResponsavelRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CadastroService {

    private final AtletaRepository atletaRepository;
    private final ResponsavelRepository responsavelRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroService(AtletaRepository atletaRepository,
                           ResponsavelRepository responsavelRepository,
                           PasswordEncoder passwordEncoder) {
        this.atletaRepository = atletaRepository;
        this.responsavelRepository = responsavelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Atleta cadastrarAtletaComResponsavel(Atleta atleta) {
        if (atleta.getResponsavel() != null) {
            Responsavel responsavelSalvo = responsavelRepository.save(atleta.getResponsavel());
            atleta.setResponsavel(responsavelSalvo);
        } else {
            throw new IllegalArgumentException("Responsável é obrigatório para o cadastro do aluno.");
        }

        atleta.setSenha(passwordEncoder.encode(atleta.getSenha()));

        if (atleta.getRoles() == null) {
            atleta.setRoles(Role.ATLETA);
        }

        return atletaRepository.save(atleta);
    }
    public List<Atleta> listarTodosAtletas() {
        return atletaRepository.findAll();
    }
}
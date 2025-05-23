package com.br.Service;

import com.br.Entity.Aluno;
import com.br.Entity.Responsavel;
import com.br.Repository.AlunoRepository;
import com.br.Repository.ResponsavelRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Aluno cadastroAluno(Aluno aluno) {
        Aluno saved = alunoRepository.save(aluno);
        entityManager.flush();  // força o envio para o banco agora
        return saved;
    }

    @Transactional
    public Responsavel cadastroResponsavel(Responsavel responsavel) {
        Responsavel saved = responsavelRepository.save(responsavel);
        entityManager.flush();
        return saved;
    }
}
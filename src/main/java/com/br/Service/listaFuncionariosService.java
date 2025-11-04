package com.br.Service;

import com.br.Enums.role;
import com.br.Repository.funcionarioRepository; // Novo e único repositório
import com.br.Entity.funcionario; // Importação da nova entidade Funcionario
import com.br.Response.funcionarioListagemResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class listaFuncionariosService {

    private final funcionarioRepository funcionarioRepository; // Apenas um repositório

    @Autowired
    public listaFuncionariosService(funcionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    // 1. Unificação do método de atualização
    @Transactional
    public  void atualizarFuncionario(funcionarioListagemResponse dto) {
        // Busca o funcionário pelo ID, independente da Role
        funcionarioRepository.findById(dto.getId()).ifPresent(funcionario -> {
            // Atualiza os campos do funcionário
            funcionario.setNome(dto.getNome());
            funcionario.setEmail(dto.getEmail());
            funcionario.setDataNascimento(dto.getDataNascimento());
            funcionario.setCpf(dto.getCpf());

            // Assume que a entidade Funcionario tem um setter para a Role
            funcionario.setRole(dto.getRole());

            funcionario.setTelefone(dto.getTelefone());

            // Salva a entidade única
            funcionarioRepository.save(funcionario);
        });

        // Não é mais necessário o switch, pois a busca é unificada.
        // Se o funcionário não for encontrado, o ifPresent simplesmente não executa.
        // Poderia adicionar um 'else' para lançar uma exceção se necessário.
    }

    // 2. Unificação do método de deleção
    @Transactional
    public void deletarFuncionario(UUID id, role role) {
        funcionarioRepository.deleteById(id);

    }
}
package com.br.Controller;

import com.br.Entity.funcionario; // A única entidade necessária
import com.br.Service.cadastroFuncionariosService; // Seu serviço de cadastro
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/cadastro")// Endpoint unificado para cadastro de funcionários
public class cadastroFuncionarioController {

    private final cadastroFuncionariosService cadastroFuncionariosService;
    // O ObjectMapper não é mais necessário, pois não há conversão entre subclasses

    @Autowired
    public cadastroFuncionarioController(cadastroFuncionariosService cadastroFuncionariosService) {
        this.cadastroFuncionariosService = cadastroFuncionariosService;
    }

    @PostMapping("/funcionarios")
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody funcionario novoFuncionario) {
        // Assume que a entidade 'funcionario' tem o método getRoles() para obter o Enum.
        if (novoFuncionario.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo 'roles' é obrigatório.");
        }

        try {
            // Não é mais necessário usar o switch/ObjectMapper, pois a única entidade é Funcionario.
            // O serviço deve validar a role e salvar.
            funcionario savedFuncionario = cadastroFuncionariosService.cadastrar(novoFuncionario);

            // Retorna a entidade Funcionario salva
            return new ResponseEntity<>(savedFuncionario, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Captura exceções lançadas pelo serviço (ex: role inválida, senha obrigatória)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erro interno ao cadastrar funcionário: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
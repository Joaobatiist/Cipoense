
package com.br.Controller;

import com.br.Entity.Coordenador;
import com.br.Entity.Super; // A superclasse comum
import com.br.Entity.Supervisor;
import com.br.Entity.Tecnico;
import com.br.Service.CadastroFuncionariosService; // Seu serviço de cadastro
import com.fasterxml.jackson.databind.ObjectMapper; // Importe ObjectMapper
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; // Para injeção de dependências
import org.springframework.web.server.ResponseStatusException; // Para lançar exceções HTTP

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/cadastro")
@CrossOrigin("http://192.168.0.10:8081")// Endpoint unificado para cadastro de funcionários
public class CadastroFuncionarioController {

    private final CadastroFuncionariosService cadastroFuncionariosService;
    private final ObjectMapper objectMapper; // Para converter o objeto genérico para o tipo específico

    @Autowired
    public CadastroFuncionarioController(CadastroFuncionariosService cadastroFuncionariosService,
                                         ObjectMapper objectMapper) {
        this.cadastroFuncionariosService = cadastroFuncionariosService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/funcionarios")
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody Super funcionarioGenerico) {
        if (funcionarioGenerico.getRoles() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo 'roles' é obrigatório.");
        }

        try {
            // Usa o 'roles' do JSON para determinar qual subclasse criar
            switch (funcionarioGenerico.getRoles()) {
                case TECNICO:
                    // Converte o JSON genérico para um objeto Tecnico
                    Tecnico tecnico = objectMapper.convertValue(funcionarioGenerico, Tecnico.class);
                    Tecnico savedTecnico = cadastroFuncionariosService.cadastrarTecnico(tecnico);
                    return new ResponseEntity<>(savedTecnico, HttpStatus.CREATED);
                case SUPERVISOR:
                    // Converte o JSON genérico para um objeto Supervisor
                    Supervisor supervisor = objectMapper.convertValue(funcionarioGenerico, Supervisor.class);
                    Supervisor savedSupervisor = cadastroFuncionariosService.cadastrarSupervisor(supervisor);
                    return new ResponseEntity<>(savedSupervisor, HttpStatus.CREATED);
                case COORDENADOR:
                    // Converte o JSON genérico para um objeto Coordenador
                    Coordenador coordenador = objectMapper.convertValue(funcionarioGenerico, Coordenador.class);
                    Coordenador savedCoordenador = cadastroFuncionariosService.cadastrarCoordenador(coordenador);
                    return new ResponseEntity<>(savedCoordenador, HttpStatus.CREATED);
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cargo de funcionário inválido: " + funcionarioGenerico.getRoles());
            }
        } catch (IllegalArgumentException e) {
            // Captura exceções lançadas pelo serviço (ex: senha obrigatória)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erro interno ao cadastrar funcionário: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
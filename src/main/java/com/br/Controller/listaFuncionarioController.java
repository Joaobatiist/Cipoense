package com.br.Controller;

import com.br.Enums.role;
import com.br.Service.listaFuncionariosService;
import com.br.Response.funcionarioListagemResponse;
import com.br.Repository.funcionarioRepository; // Novo e único repositório
import com.br.Entity.funcionario; // Entidade Funcionario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/funcionarios")
public class listaFuncionarioController {

    private final funcionarioRepository funcionarioRepository; // Apenas o repositório unificado
    private final listaFuncionariosService listaFuncionarios;

    @Autowired
    public listaFuncionarioController(funcionarioRepository funcionarioRepository,
                                      listaFuncionariosService listaFuncionarios) {
        this.funcionarioRepository = funcionarioRepository;
        this.listaFuncionarios = listaFuncionarios;
    }

    // 1. Método de listagem unificado
    @GetMapping("/listarfuncionarios")
    public ResponseEntity<List<funcionarioListagemResponse>> listarFuncionarios() {
        // Busca todos os registros na tabela única 'funcionario'
        List<funcionarioListagemResponse> todosFuncionarios = funcionarioRepository.findAll().stream()
                .map(funcionario -> new funcionarioListagemResponse(
                        funcionario.getRole().name(),
                        funcionario.getId(),
                        funcionario.getNome(),
                        funcionario.getCpf(),
                        funcionario.getDataNascimento(),
                        funcionario.getEmail(),
                        funcionario.getRole(),
                        funcionario.getTelefone()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(todosFuncionarios);
    }

    // 2. Método de atualização (inalterado, pois já usa o Service unificado)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable UUID id, @RequestBody funcionarioListagemResponse dto) {
        try {
            dto.setId(id);

            if (dto.getRole() == null) {
                return ResponseEntity.badRequest().body("O tipo (role) do funcionário deve ser informado.");
            }

            // O Service já foi refatorado para lidar com o objeto Funcionario Listagem Response
            listaFuncionarios.atualizarFuncionario(dto);
            System.out.println(dto.getRole());
            return ResponseEntity.ok("Funcionário atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar funcionário.");
        }
    }

    // 3. Método de deleção (mantendo a chamada ao Service, que já foi refatorado)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFuncionario(@PathVariable UUID id, @RequestParam role roles) {
        try {
            // No service refatorado, o parâmetro 'roles' é redundante, mas mantemos a assinatura por enquanto.
            listaFuncionarios.deletarFuncionario(id, roles);
            return ResponseEntity.ok("Funcionário excluído com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir funcionário.");
        }
    }
}
package com.br.Controller;



import com.br.Enums.role;
import com.br.Service.listaFuncionariosService;
import com.br.Response.funcionarioListagemResponse;
import com.br.Repository.coordenadorRepository;
import com.br.Repository.supervisorRepository;
import com.br.Repository.tecnicoRepository;
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

    private final tecnicoRepository tecnicoRepository;
    private final coordenadorRepository coordenadorRepository;
    private final supervisorRepository supervisorRepository;


    private final listaFuncionariosService  listaFuncionarios;

    @Autowired
    public listaFuncionarioController(tecnicoRepository tecnicoRepository,
                                      coordenadorRepository coordenadorRepository,
                                      supervisorRepository supervisorRepository,
                                      listaFuncionariosService listaFuncionarios) {
        this.tecnicoRepository = tecnicoRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.supervisorRepository = supervisorRepository;
        this.listaFuncionarios = listaFuncionarios;
    }

    @GetMapping("/listarfuncionarios")
    public ResponseEntity<List<funcionarioListagemResponse>> listarFuncionarios(){
    List<funcionarioListagemResponse> todosFuncionarios = Stream.of(
                          tecnicoRepository.findAll().stream()
                                  .map(tecnico -> new funcionarioListagemResponse(tecnico.getRoles().name(), tecnico.getId(),tecnico.getNome(), tecnico.getCpf(),
                                          tecnico.getDataNascimento(), tecnico.getEmail(), tecnico.getRoles(), tecnico.getTelefone())),
                          coordenadorRepository.findAll().stream()
                                  .map(coordenador -> new funcionarioListagemResponse(coordenador.getRoles().name(),coordenador.getId(),coordenador.getNome(), coordenador.getCpf(),
                                          coordenador.getDataNascimento(),
                                          coordenador.getEmail(), coordenador.getRoles(), coordenador.getTelefone())),
                          supervisorRepository.findAll().stream()
                                  .map(supervisor -> new funcionarioListagemResponse(supervisor.getRoles().name(),supervisor.getId(),supervisor.getNome(), supervisor.getCpf(),
                                          supervisor.getDataNascimento(), supervisor.getEmail(), supervisor.getRoles(), supervisor.getTelefone()))
                  )
                  .flatMap(stream -> stream)
                  .collect(Collectors.toList());

        return ResponseEntity.ok(todosFuncionarios);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable UUID id, @RequestBody funcionarioListagemResponse dto) {
        try {

            dto.setId(id);

            if (dto.getRoles() == null) {
                return ResponseEntity.badRequest().body("O tipo (role) do funcionário deve ser informado.");
            }

            listaFuncionarios.atualizarFuncionario(dto);
            System.out.println(dto.getRoles());
            return ResponseEntity.ok("Funcionário atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar funcionário.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFuncionario(@PathVariable UUID id, @RequestParam role roles) {
        try {
            listaFuncionarios.deletarFuncionario(id, roles);
            return ResponseEntity.ok("Funcionário excluído com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir funcionário.");
        }
    }
}

package com.br.Controller;


import com.br.Entity.Aluno;
import com.br.Entity.Responsavel;
import com.br.Service.CadastroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://192.168.0.10:8081/alunos")
public class CadastroController {

    @Autowired
    private CadastroService cadastroService;

    @PostMapping("/alunos")
    public Aluno cadastrarNovoAluno(@RequestBody Aluno aluno) {
        System.out.println("Recebido: " + aluno);
        return cadastroService.cadastroAluno(aluno);
    }

    @PostMapping()
    public Responsavel cadastrarNovoResponsavel(@RequestBody Responsavel responsavel) {
        return cadastroService.cadastroResponsavel(responsavel);
    }


}